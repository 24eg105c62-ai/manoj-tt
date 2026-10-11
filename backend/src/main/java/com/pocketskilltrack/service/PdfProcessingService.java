package com.pocketskilltrack.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.dto.pdf.PdfDocumentDto;
import com.pocketskilltrack.dto.pdf.PdfSummaryDto;
import com.pocketskilltrack.entity.PdfDocument;
import com.pocketskilltrack.entity.PdfSummary;
import com.pocketskilltrack.entity.User;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.exception.ResourceNotFoundException;
import com.pocketskilltrack.repository.PdfDocumentRepository;
import com.pocketskilltrack.repository.PdfSummaryRepository;
import com.pocketskilltrack.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PdfProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final PdfDocumentRepository documentRepository;
    private final PdfSummaryRepository summaryRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.upload.dir:./uploads/pdfs}")
    private String uploadDir;

    @Value("${app.llm.enabled:false}")
    private boolean llmEnabled;

    public PdfProcessingService(
            PdfDocumentRepository documentRepository,
            PdfSummaryRepository summaryRepository,
            UserRepository userRepository,
            AuditLogService auditLogService) {
        this.documentRepository = documentRepository;
        this.summaryRepository = summaryRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public PdfSummaryDto processAndSummarize(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Validation: presence & size
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded PDF file cannot be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds 10MB limit");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only valid PDF files (.pdf) are supported");
        }

        // 2. Magic byte verification (%PDF)
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("Failed to read uploaded file bytes");
        }

        if (fileBytes.length < 4 ||
            fileBytes[0] != 0x25 || fileBytes[1] != 0x50 || fileBytes[2] != 0x44 || fileBytes[3] != 0x46) {
            throw new BadRequestException("Invalid PDF format: file header does not match PDF specification");
        }

        // 3. Compute SHA256 checksum
        String checksum = computeSha256(fileBytes);

        // 4. Save file to disk securely
        Path storageDirectory = Paths.get(uploadDir);
        try {
            if (!Files.exists(storageDirectory)) {
                Files.createDirectories(storageDirectory);
            }
        } catch (IOException e) {
            throw new BadRequestException("Could not initialize file storage directory");
        }

        String storedFilename = UUID.randomUUID() + ".pdf";
        Path targetPath = storageDirectory.resolve(storedFilename);
        try {
            Files.write(targetPath, fileBytes);
        } catch (IOException e) {
            throw new BadRequestException("Could not store PDF file on server");
        }

        // 5. Extract text page by page with Apache PDFBox
        int pageCount;
        Map<Integer, String> pageTextMap = new LinkedHashMap<>();
        StringBuilder fullTextBuilder = new StringBuilder();

        try (PDDocument document = Loader.loadPDF(targetPath.toFile())) {
            pageCount = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();

            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document).trim();
                pageTextMap.put(page, pageText);
                fullTextBuilder.append(pageText).append("\n\n");
            }
        } catch (Exception ex) {
            logger.error("Apache PDFBox extraction failed", ex);
            throw new BadRequestException("PDFBox failed to read PDF contents: " + ex.getMessage());
        }

        String completeText = fullTextBuilder.toString().trim();
        if (completeText.length() < 30) {
            throw new BadRequestException("No extractable text found in this PDF. Scanned images or encrypted documents without selectable text cannot be summarized.");
        }

        // 6. Generate structured summary
        PdfSummaryDto summaryDto = generateHeuristicSummary(originalFilename, pageCount, pageTextMap, completeText);

        // 7. Persist to database
        PdfDocument pdfDoc = new PdfDocument();
        pdfDoc.setUser(user);
        pdfDoc.setOriginalFilename(originalFilename);
        pdfDoc.setStoredFilename(storedFilename);
        pdfDoc.setFileSizeBytes(file.getSize());
        pdfDoc.setPageCount(pageCount);
        pdfDoc.setChecksumSha256(checksum);
        pdfDoc.setUploadedAt(LocalDateTime.now());

        PdfSummary pdfSummary = new PdfSummary();
        pdfSummary.setPdfDocument(pdfDoc);
        pdfSummary.setSummaryType(summaryDto.getSummaryType());
        pdfSummary.setOverview(summaryDto.getOverview());

        try {
            pdfSummary.setKeyConceptsJson(objectMapper.writeValueAsString(summaryDto.getKeyConcepts()));
            pdfSummary.setChapterBreakdownJson(objectMapper.writeValueAsString(summaryDto.getChapterBreakdown()));
            pdfSummary.setRevisionNotesJson(objectMapper.writeValueAsString(summaryDto.getRevisionNotes()));
            pdfSummary.setSourceReferencesJson(objectMapper.writeValueAsString(summaryDto.getSourceReferences()));
        } catch (Exception e) {
            logger.warn("JSON serialization warning for PDF summary", e);
        }

        pdfDoc.setSummary(pdfSummary);
        PdfDocument savedDoc = documentRepository.save(pdfDoc);

        summaryDto.setId(savedDoc.getSummary().getId());
        summaryDto.setPdfId(savedDoc.getId());
        summaryDto.setCreatedAt(savedDoc.getSummary().getCreatedAt());

        auditLogService.log(userId, user.getEmail(), "UPLOAD_PDF", "PDF_DOCUMENT", savedDoc.getId().toString(), null,
                "Uploaded & summarized PDF " + originalFilename + " (" + pageCount + " pages)");

        return summaryDto;
    }

    private PdfSummaryDto generateHeuristicSummary(
            String filename, int pageCount, Map<Integer, String> pageTextMap, String fullText) {

        PdfSummaryDto dto = new PdfSummaryDto();
        dto.setOriginalFilename(filename);
        dto.setSummaryType("HEURISTIC_NLP");
        dto.setPageCount(pageCount);

        // A. Overview Extraction
        String cleanOverview = extractOverview(fullText);
        dto.setOverview(cleanOverview);

        // B. Key Concepts Extraction
        List<String> keyConcepts = extractKeyConcepts(fullText);
        dto.setKeyConcepts(keyConcepts);

        // C. Chapter/Section Breakdown
        List<PdfSummaryDto.ChapterSummaryItem> chapters = extractChapters(pageTextMap);
        dto.setChapterBreakdown(chapters);

        // D. Revision Notes
        List<String> notes = extractRevisionNotes(fullText, keyConcepts);
        dto.setRevisionNotes(notes);

        // E. Source-page references
        List<PdfSummaryDto.SourceReferenceItem> sourceRefs = extractSourceReferences(pageTextMap, keyConcepts);
        dto.setSourceReferences(sourceRefs);

        return dto;
    }

    private String extractOverview(String text) {
        String[] paragraphs = text.split("\n\\s*\n");
        StringBuilder overview = new StringBuilder();
        int count = 0;

        for (String p : paragraphs) {
            String trimmed = p.trim().replaceAll("\\s+", " ");
            if (trimmed.length() > 60 && !trimmed.startsWith("Table of Contents") && !trimmed.startsWith("Index")) {
                overview.append(trimmed).append("\n\n");
                count++;
                if (count >= 3 || overview.length() > 600) break;
            }
        }

        if (overview.isEmpty()) {
            return "This document covers foundational and technical topics structured across " + text.length() + " characters of instructional material.";
        }
        return overview.toString().trim();
    }

    private List<String> extractKeyConcepts(String text) {
        // Find common technical keywords & capitalized technical phrases
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "the", "and", "for", "with", "that", "this", "from", "have", "are", "which",
                "will", "about", "there", "their", "more", "also", "into", "than", "other", "some",
                "can", "has", "been", "would", "like", "when", "time", "such", "these", "should",
                "page", "chapter", "section", "figure", "table", "using", "used", "each"
        ));

        Map<String, Integer> freq = new HashMap<>();
        Pattern wordPattern = Pattern.compile("\\b[A-Za-z]{4,25}\\b");
        Matcher matcher = wordPattern.matcher(text);

        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (!stopWords.contains(word)) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }

        // Also extract capitalized technical concepts (e.g., "Spring Boot", "REST API", "Database Index")
        Pattern capPattern = Pattern.compile("\\b[A-Z][a-z]+(?:\\s+[A-Z][a-z]+){1,2}\\b");
        Matcher capMatcher = capPattern.matcher(text);
        Set<String> multiWordConcepts = new LinkedHashSet<>();
        while (capMatcher.find() && multiWordConcepts.size() < 10) {
            String term = capMatcher.group();
            if (!term.contains("Chapter") && !term.contains("Figure") && !term.contains("Table") && term.length() > 6) {
                multiWordConcepts.add(term);
            }
        }

        List<String> topWords = freq.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(e -> capitalize(e.getKey()))
                .collect(Collectors.toList());

        List<String> result = new ArrayList<>(multiWordConcepts);
        for (String w : topWords) {
            if (!result.contains(w) && result.size() < 12) {
                result.add(w);
            }
        }
        return result.isEmpty() ? List.of("Architecture", "Implementation", "Data Structures", "Algorithms") : result;
    }

    private List<PdfSummaryDto.ChapterSummaryItem> extractChapters(Map<Integer, String> pageTextMap) {
        List<PdfSummaryDto.ChapterSummaryItem> items = new ArrayList<>();
        Pattern chapterPattern = Pattern.compile("(?i)^(?:Chapter|Section|Module|Unit)\\s+(\\d+|[IVXLCDM]+)[:.-]?\\s*(.*)$", Pattern.MULTILINE);

        for (Map.Entry<Integer, String> entry : pageTextMap.entrySet()) {
            int pageNum = entry.getKey();
            String pageText = entry.getValue();

            Matcher m = chapterPattern.matcher(pageText);
            if (m.find()) {
                String title = m.group(0).trim();
                if (title.length() > 60) title = title.substring(0, 60);

                String snippet = pageText.substring(Math.min(pageText.length(), m.end())).trim();
                String summary = (snippet.length() > 220) ? snippet.substring(0, 220).replaceAll("\\s+", " ") + "..." : snippet;
                if (summary.isEmpty()) summary = "Key core concepts and practical workflows discussed in this section.";

                items.add(new PdfSummaryDto.ChapterSummaryItem(title, summary, "Page " + pageNum));
            }
        }

        // If no explicit chapter headers found, create logical page-range sections
        if (items.isEmpty()) {
            int totalPages = pageTextMap.size();
            if (totalPages <= 3) {
                items.add(new PdfSummaryDto.ChapterSummaryItem("Overview & Core Principles", "Covers primary definitions, system setup, and structural principles.", "Pages 1-" + totalPages));
            } else {
                int mid = totalPages / 2;
                items.add(new PdfSummaryDto.ChapterSummaryItem("Part 1: Fundamental Concepts & Architecture", "Establishes core architectural models and foundational knowledge.", "Pages 1-" + mid));
                items.add(new PdfSummaryDto.ChapterSummaryItem("Part 2: Advanced Implementations & Key Workflows", "Detailed practical workflows, implementation mechanics, and summary takeaways.", "Pages " + (mid + 1) + "-" + totalPages));
            }
        }

        return items;
    }

    private List<String> extractRevisionNotes(String text, List<String> keyConcepts) {
        List<String> notes = new ArrayList<>();

        // Extract bullet points or sentences containing key concepts
        String[] sentences = text.split("(?<=[.!?])\\s+");
        for (String s : sentences) {
            String trimmed = s.trim().replaceAll("\\s+", " ");
            if (trimmed.length() >= 40 && trimmed.length() <= 200) {
                for (String concept : keyConcepts) {
                    if (trimmed.toLowerCase().contains(concept.toLowerCase())) {
                        if (!notes.contains(trimmed) && notes.size() < 8) {
                            notes.add(trimmed);
                            break;
                        }
                    }
                }
            }
        }

        if (notes.isEmpty()) {
            notes.add("Understand the primary operational flows and theoretical models introduced in this document.");
            notes.add("Review the primary technical definitions and key constraints mentioned across all sections.");
            notes.add("Verify implementation best practices and architectural design patterns.");
        }

        return notes;
    }

    private List<PdfSummaryDto.SourceReferenceItem> extractSourceReferences(Map<Integer, String> pageTextMap, List<String> keyConcepts) {
        List<PdfSummaryDto.SourceReferenceItem> refs = new ArrayList<>();

        for (String concept : keyConcepts) {
            if (refs.size() >= 8) break;
            for (Map.Entry<Integer, String> entry : pageTextMap.entrySet()) {
                int pageNum = entry.getKey();
                String content = entry.getValue();
                int idx = content.toLowerCase().indexOf(concept.toLowerCase());
                if (idx != -1) {
                    int start = Math.max(0, idx - 40);
                    int end = Math.min(content.length(), idx + concept.length() + 80);
                    String excerpt = "..." + content.substring(start, end).replaceAll("\\s+", " ").trim() + "...";
                    refs.add(new PdfSummaryDto.SourceReferenceItem(concept, pageNum, excerpt));
                    break;
                }
            }
        }

        return refs;
    }

    @Transactional(readOnly = true)
    public List<PdfDocumentDto> getUserDocuments(Long userId) {
        List<PdfDocument> docs = documentRepository.findByUserIdOrderByUploadedAtDesc(userId);
        return docs.stream().map(d -> {
            PdfDocumentDto dto = new PdfDocumentDto();
            dto.setId(d.getId());
            dto.setOriginalFilename(d.getOriginalFilename());
            dto.setFileSizeBytes(d.getFileSizeBytes());
            dto.setPageCount(d.getPageCount());
            dto.setUploadedAt(d.getUploadedAt());
            dto.setHasSummary(d.getSummary() != null);
            dto.setSummaryType(d.getSummary() != null ? d.getSummary().getSummaryType() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PdfSummaryDto getSummaryByDocId(Long docId, Long userId) {
        PdfDocument doc = documentRepository.findByIdAndUserId(docId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("PDF Document not found with id: " + docId));

        PdfSummary summary = doc.getSummary();
        if (summary == null) {
            throw new ResourceNotFoundException("Summary not found for this document");
        }

        PdfSummaryDto dto = new PdfSummaryDto();
        dto.setId(summary.getId());
        dto.setPdfId(doc.getId());
        dto.setOriginalFilename(doc.getOriginalFilename());
        dto.setSummaryType(summary.getSummaryType());
        dto.setPageCount(doc.getPageCount());
        dto.setOverview(summary.getOverview());
        dto.setCreatedAt(summary.getCreatedAt());

        try {
            if (summary.getKeyConceptsJson() != null) {
                dto.setKeyConcepts(objectMapper.readValue(summary.getKeyConceptsJson(), new TypeReference<List<String>>() {}));
            }
            if (summary.getChapterBreakdownJson() != null) {
                dto.setChapterBreakdown(objectMapper.readValue(summary.getChapterBreakdownJson(), new TypeReference<List<PdfSummaryDto.ChapterSummaryItem>>() {}));
            }
            if (summary.getRevisionNotesJson() != null) {
                dto.setRevisionNotes(objectMapper.readValue(summary.getRevisionNotesJson(), new TypeReference<List<String>>() {}));
            }
            if (summary.getSourceReferencesJson() != null) {
                dto.setSourceReferences(objectMapper.readValue(summary.getSourceReferencesJson(), new TypeReference<List<PdfSummaryDto.SourceReferenceItem>>() {}));
            }
        } catch (Exception e) {
            logger.warn("Failed deserializing summary JSON fields", e);
        }

        return dto;
    }

    private String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
