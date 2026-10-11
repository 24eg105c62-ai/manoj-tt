package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.PdfDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {
    List<PdfDocument> findByUserIdOrderByUploadedAtDesc(Long userId);
    Page<PdfDocument> findByUserIdOrderByUploadedAtDesc(Long userId, Pageable pageable);
    Optional<PdfDocument> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
