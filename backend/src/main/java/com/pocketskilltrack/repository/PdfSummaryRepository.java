package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.PdfSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdfSummaryRepository extends JpaRepository<PdfSummary, Long> {
    Optional<PdfSummary> findByPdfDocumentId(Long pdfDocumentId);
}
