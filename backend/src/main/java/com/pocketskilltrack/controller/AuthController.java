package com.pocketskilltrack.controller;

import com.pocketskilltrack.dto.auth.*;
import com.pocketskilltrack.dto.common.ApiResponse;
import com.pocketskilltrack.security.UserPrincipal;
import com.pocketskilltrack.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse profile = authService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = authService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }
}
