package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.dto.dashboard.StudentDashboardDto;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<StudentDashboardDto>> getDashboardOverview(
            @AuthenticationPrincipal UserPrincipal principal) {
        StudentDashboardDto dto = dashboardService.getDashboardData(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
