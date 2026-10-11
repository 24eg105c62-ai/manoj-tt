package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.pdf.PdfDocumentDto;
import com.pocketskilltrack.dto.pdf.PdfSummaryDto;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.PdfProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pdf")
public class PdfController {

    private final PdfProcessingService pdfProcessingService;

    public PdfController(PdfProcessingService pdfProcessingService) {
        this.pdfProcessingService = pdfProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<PdfSummaryDto>> uploadAndSummarize(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        PdfSummaryDto summary = pdfProcessingService.processAndSummarize(file, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("PDF processed and summarized successfully", summary));
    }

    @GetMapping("/my-documents")
    public ResponseEntity<ApiResponse<List<PdfDocumentDto>>> getMyDocuments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<PdfDocumentDto> docs = pdfProcessingService.getUserDocuments(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(docs));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<PdfSummaryDto>> getSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        PdfSummaryDto summary = pdfProcessingService.getSummaryByDocId(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
