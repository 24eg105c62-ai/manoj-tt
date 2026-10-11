package com.pocketskilltrack.service;

import com.pocketskilltrack.dto.auth.*;
import com.pocketskilltrack.entity.Role;
import com.pocketskilltrack.entity.RoleName;
import com.pocketskilltrack.entity.User;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.exception.ResourceNotFoundException;
import com.pocketskilltrack.repository.RoleRepository;
import com.pocketskilltrack.repository.UserRepository;
import com.pocketskilltrack.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already in use");
        }
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new BadRequestException("Student ID is already registered");
        }

        User user = new User();
        user.setStudentId(request.getStudentId().trim().toUpperCase());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setCollege(request.getCollege());
        user.setDepartment(request.getDepartment());
        user.setStudyYear(request.getStudyYear());
        user.setBio(request.getBio());
        user.setEnabled(true);

        // Security rule: Backend strictly assigns ROLE_STUDENT to all public registrations
        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_STUDENT)));
        user.setRoles(Collections.singleton(studentRole));

        User savedUser = userRepository.save(user);

        // Auto authenticate after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        auditLogService.log(savedUser.getId(), savedUser.getEmail(), "REGISTER", "USER", savedUser.getId().toString(), null, "Student registered successfully");

        return new AuthResponse(jwt, mapToProfile(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.getEmailOrStudentId().trim();

        // If identifier is student ID, resolve to email for Spring Security
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByStudentId(identifier.toUpperCase()))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        auditLogService.log(user.getId(), user.getEmail(), "LOGIN", "USER", user.getId().toString(), null, "User logged in successfully");

        return new AuthResponse(jwt, mapToProfile(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToProfile(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setFullName(request.getFullName().trim());
        user.setCollege(request.getCollege());
        user.setDepartment(request.getDepartment());
        user.setStudyYear(request.getStudyYear());
        user.setBio(request.getBio());

        User updatedUser = userRepository.save(user);

        auditLogService.log(user.getId(), user.getEmail(), "UPDATE_PROFILE", "USER", user.getId().toString(), null, "Profile updated");

        return mapToProfile(updatedUser);
    }

    public UserProfileResponse mapToProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getStudentId(),
                user.getEmail(),
                user.getFullName(),
                user.getCollege(),
                user.getDepartment(),
                user.getStudyYear(),
                user.getBio(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}
