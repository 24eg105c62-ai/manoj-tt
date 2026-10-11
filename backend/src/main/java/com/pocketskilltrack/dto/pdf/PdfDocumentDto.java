package com.pocketskilltrack.dto.pdf;

import java.time.LocalDateTime;

public class PdfDocumentDto {

    private Long id;
    private String originalFilename;
    private long fileSizeBytes;
    private int pageCount;
    private LocalDateTime uploadedAt;
    private boolean hasSummary;
    private String summaryType;

    public PdfDocumentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public boolean isHasSummary() { return hasSummary; }
    public void setHasSummary(boolean hasSummary) { this.hasSummary = hasSummary; }

    public String getSummaryType() { return summaryType; }
    public void setSummaryType(String summaryType) { this.summaryType = summaryType; }
}
