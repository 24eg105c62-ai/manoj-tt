package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.prediction.ScorePredictionDto;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.ScorePredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/predictions")
public class ScorePredictionController {

    private final ScorePredictionService scorePredictionService;

    public ScorePredictionController(ScorePredictionService scorePredictionService) {
        this.scorePredictionService = scorePredictionService;
    }

    @GetMapping({"", "/", "/my-prediction"})
    public ResponseEntity<ApiResponse<ScorePredictionDto>> getMyPrediction(
            @AuthenticationPrincipal UserPrincipal principal) {
        ScorePredictionDto dto = scorePredictionService.calculateStudentPrediction(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
