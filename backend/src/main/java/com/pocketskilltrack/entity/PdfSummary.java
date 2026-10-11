package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "summaries")
public class PdfSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_id", nullable = false, unique = true)
    @JsonIgnore
    private PdfDocument pdfDocument;

    @Column(name = "summary_type", nullable = false, length = 30)
    private String summaryType = "HEURISTIC";

    @Column(name = "overview", columnDefinition = "LONGTEXT", nullable = false)
    private String overview;

    @Column(name = "key_concepts_json", columnDefinition = "LONGTEXT")
    private String keyConceptsJson;

    @Column(name = "chapter_breakdown_json", columnDefinition = "LONGTEXT")
    private String chapterBreakdownJson;

    @Column(name = "revision_notes_json", columnDefinition = "LONGTEXT")
    private String revisionNotesJson;

    @Column(name = "source_references_json", columnDefinition = "LONGTEXT")
    private String sourceReferencesJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PdfSummary() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdfDocument getPdfDocument() { return pdfDocument; }
    public void setPdfDocument(PdfDocument pdfDocument) { this.pdfDocument = pdfDocument; }

    public String getSummaryType() { return summaryType; }
    public void setSummaryType(String summaryType) { this.summaryType = summaryType; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getKeyConceptsJson() { return keyConceptsJson; }
    public void setKeyConceptsJson(String keyConceptsJson) { this.keyConceptsJson = keyConceptsJson; }

    public String getChapterBreakdownJson() { return chapterBreakdownJson; }
    public void setChapterBreakdownJson(String chapterBreakdownJson) { this.chapterBreakdownJson = chapterBreakdownJson; }

    public String getRevisionNotesJson() { return revisionNotesJson; }
    public void setRevisionNotesJson(String revisionNotesJson) { this.revisionNotesJson = revisionNotesJson; }

    public String getSourceReferencesJson() { return sourceReferencesJson; }
    public void setSourceReferencesJson(String sourceReferencesJson) { this.sourceReferencesJson = sourceReferencesJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
