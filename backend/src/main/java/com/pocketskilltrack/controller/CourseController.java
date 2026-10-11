package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.dto.course.*;
import com.pocketskilltrack.entity.DifficultyLevel;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.CourseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CourseDto>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) DifficultyLevel difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userId = principal != null ? principal.getId() : null;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<CourseDto> response = courseService.getCourses(search, category, difficulty, userId, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDetailDto>> getCourseDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        CourseDetailDto detail = courseService.getCourseDetail(id, userId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentDto>> enroll(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        EnrollmentDto enrollment = courseService.enroll(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Successfully enrolled in course", enrollment));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean bookmarked = courseService.toggleBookmark(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("bookmarked", bookmarked)));
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<ApiResponse<TopicDetailDto>> getTopicDetail(
            @PathVariable Long topicId,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : null;
        TopicDetailDto dto = courseService.getTopicDetail(topicId, userId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("/topics/{topicId}/complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeTopic(
            @PathVariable Long topicId,
            @AuthenticationPrincipal UserPrincipal principal) {
        int progress = courseService.completeTopic(topicId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Topic marked as completed", Map.of("progressPercent", progress)));
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<EnrollmentDto> list = courseService.getUserEnrollments(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
