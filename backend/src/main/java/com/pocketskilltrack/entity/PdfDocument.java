package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pdf_documents", indexes = {
    @Index(name = "idx_pdf_user", columnList = "user_id")
})
public class PdfDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "page_count", nullable = false)
    private int pageCount = 0;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "pdfDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private PdfSummary summary;

    public PdfDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public PdfSummary getSummary() { return summary; }
    public void setSummary(PdfSummary summary) { this.summary = summary; }
}
