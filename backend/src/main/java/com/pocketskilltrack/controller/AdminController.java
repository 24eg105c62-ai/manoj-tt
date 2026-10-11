package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.admin.*;
import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.entity.Achievement;
import com.pocketskilltrack.entity.Course;
import com.pocketskilltrack.entity.CourseModule;
import com.pocketskilltrack.entity.Question;
import com.pocketskilltrack.entity.Topic;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AdminOverviewDto>> getOverview() {
        AdminOverviewDto overview = adminService.getOverview();
        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserAdminDto>>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<UserAdminDto> users = adminService.getUsers(search, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleUserStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        adminService.toggleUserStatus(id, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("User status changed", Map.of("status", "SUCCESS")));
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<Course>> createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Course course = adminService.createCourse(request, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Course created successfully", course));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Course course = adminService.updateCourse(id, request, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", course));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        adminService.deleteCourse(id, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", Map.of("status", "DELETED")));
    }

    @PostMapping("/courses/{id}/modules")
    public ResponseEntity<ApiResponse<CourseModule>> addModule(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        String title = (String) body.get("title");
        int order = body.containsKey("moduleOrder") ? (int) body.get("moduleOrder") : 1;
        CourseModule module = adminService.addModule(id, title, order, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Module added", module));
    }

    @PostMapping("/modules/{moduleId}/topics")
    public ResponseEntity<ApiResponse<Topic>> addTopic(
            @PathVariable Long moduleId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        int readTime = body.containsKey("readingTimeMinutes") ? (int) body.get("readingTimeMinutes") : 5;
        int order = body.containsKey("topicOrder") ? (int) body.get("topicOrder") : 1;
        Topic topic = adminService.addTopic(moduleId, title, content, readTime, order, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Topic added", topic));
    }

    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<Question>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Question question = adminService.createQuestion(request, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Question created successfully", question));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        adminService.deleteQuestion(id, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Question deleted", Map.of("status", "DELETED")));
    }

    @PostMapping("/achievements")
    public ResponseEntity<ApiResponse<Achievement>> createAchievement(
            @Valid @RequestBody AchievementCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Achievement ach = adminService.createAchievement(request, principal.getId(), principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Badge created successfully", ach));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogDto>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<AuditLogDto> logs = adminService.getAuditLogs(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
