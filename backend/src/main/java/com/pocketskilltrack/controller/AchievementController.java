package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.achievement.*;
import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AchievementDto>>> getMyAchievements(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AchievementDto> badges = achievementService.getAllAchievementsForUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(badges));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<PagedResponse<StudentAchievementDto>>> getSocialFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("unlockedAt").descending());
        Page<StudentAchievementDto> feedPage = achievementService.getSocialFeed(principal.getId(), pageRequest);
        PagedResponse<StudentAchievementDto> response = new PagedResponse<>(
                feedPage.getContent(), feedPage.getNumber(), feedPage.getSize(),
                feedPage.getTotalElements(), feedPage.getTotalPages(), feedPage.isLast()
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/react")
    public ResponseEntity<ApiResponse<StudentAchievementDto>> react(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        StudentAchievementDto updated = achievementService.reactToAchievement(id, principal.getId(), request.getReactionType());
        return ResponseEntity.ok(ApiResponse.success("Reaction updated", updated));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getComments(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<CommentDto> comments = achievementService.getComments(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        CommentDto comment = achievementService.addComment(id, principal.getId(), request.getCommentText());
        return ResponseEntity.ok(ApiResponse.success("Comment added", comment));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        achievementService.deleteComment(commentId, principal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Comment removed", Map.of("status", "REMOVED")));
    }
}
