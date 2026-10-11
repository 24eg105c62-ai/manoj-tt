package com.pocketskilltrack.dto.pdf;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PdfSummaryDto {

    private Long id;
    private Long pdfId;
    private String originalFilename;
    private String summaryType;
    private int pageCount;
    private String overview;
    private List<String> keyConcepts = new ArrayList<>();
    private List<ChapterSummaryItem> chapterBreakdown = new ArrayList<>();
    private List<String> revisionNotes = new ArrayList<>();
    private List<SourceReferenceItem> sourceReferences = new ArrayList<>();
    private LocalDateTime createdAt;

    public PdfSummaryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPdfId() { return pdfId; }
    public void setPdfId(Long pdfId) { this.pdfId = pdfId; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getSummaryType() { return summaryType; }
    public void setSummaryType(String summaryType) { this.summaryType = summaryType; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public List<String> getKeyConcepts() { return keyConcepts; }
    public void setKeyConcepts(List<String> keyConcepts) { this.keyConcepts = keyConcepts; }

    public List<ChapterSummaryItem> getChapterBreakdown() { return chapterBreakdown; }
    public void setChapterBreakdown(List<ChapterSummaryItem> chapterBreakdown) { this.chapterBreakdown = chapterBreakdown; }

    public List<String> getRevisionNotes() { return revisionNotes; }
    public void setRevisionNotes(List<String> revisionNotes) { this.revisionNotes = revisionNotes; }

    public List<SourceReferenceItem> getSourceReferences() { return sourceReferences; }
    public void setSourceReferences(List<SourceReferenceItem> sourceReferences) { this.sourceReferences = sourceReferences; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class ChapterSummaryItem {
        private String title;
        private String summary;
        private String pageRange;

        public ChapterSummaryItem() {}

        public ChapterSummaryItem(String title, String summary, String pageRange) {
            this.title = title;
            this.summary = summary;
            this.pageRange = pageRange;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getPageRange() { return pageRange; }
        public void setPageRange(String pageRange) { this.pageRange = pageRange; }
    }

    public static class SourceReferenceItem {
        private String topic;
        private int pageNumber;
        private String excerpt;

        public SourceReferenceItem() {}

        public SourceReferenceItem(String topic, int pageNumber, String excerpt) {
            this.topic = topic;
            this.pageNumber = pageNumber;
            this.excerpt = excerpt;
        }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public int getPageNumber() { return pageNumber; }
        public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
        public String getExcerpt() { return excerpt; }
        public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    }
}
