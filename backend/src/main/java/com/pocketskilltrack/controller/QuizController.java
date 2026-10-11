package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.dto.quiz.QuizAttemptDto;
import com.pocketskilltrack.dto.quiz.QuizQuestionDto;
import com.pocketskilltrack.dto.quiz.QuizSubmissionResponse;
import com.pocketskilltrack.dto.quiz.QuizSubmitRequest;
import com.pocketskilltrack.entity.Difficulty;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<ApiResponse<List<QuizQuestionDto>>> getQuizQuestions(
            @PathVariable Long topicId,
            @RequestParam(required = false) Difficulty difficulty,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<QuizQuestionDto> questions = quizService.getQuizQuestions(topicId, difficulty, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @PostMapping("/attempts")
    public ResponseEntity<ApiResponse<QuizSubmissionResponse>> submitQuiz(
            @Valid @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        QuizSubmissionResponse response = quizService.submitQuiz(request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Quiz evaluated successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PagedResponse<QuizAttemptDto>>> getQuizHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("completedTime").descending());
        PagedResponse<QuizAttemptDto> history = quizService.getUserQuizHistoryPaged(principal.getId(), pageRequest);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
