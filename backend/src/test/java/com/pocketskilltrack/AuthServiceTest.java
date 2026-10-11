package com.pocketskilltrack;

import com.pocketskilltrack.dto.auth.AuthResponse;
import com.pocketskilltrack.dto.auth.LoginRequest;
import com.pocketskilltrack.dto.auth.RegisterRequest;
import com.pocketskilltrack.entity.Role;
import com.pocketskilltrack.entity.RoleName;
import com.pocketskilltrack.entity.User;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.repository.RoleRepository;
import com.pocketskilltrack.repository.UserRepository;
import com.pocketskilltrack.security.JwtTokenProvider;
import com.pocketskilltrack.service.AuditLogService;
import com.pocketskilltrack.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                roleRepository,
                passwordEncoder,
                authenticationManager,
                tokenProvider,
                auditLogService
        );
    }

    @Test
    void testRegisterNewStudent_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setStudentId("STU12345");
        req.setEmail("teststudent@univ.edu");
        req.setPassword("Password123!");
        req.setFullName("Alice Chen");
        req.setCollege("Engineering");
        req.setDepartment("CS");
        req.setStudyYear("Year 2");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByStudentId(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(roleRepository.findByName(RoleName.ROLE_STUDENT)).thenReturn(Optional.of(new Role(RoleName.ROLE_STUDENT)));

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setStudentId("STU12345");
        savedUser.setEmail("teststudent@univ.edu");
        savedUser.setFullName("Alice Chen");
        savedUser.setRoles(Collections.singleton(new Role(RoleName.ROLE_STUDENT)));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(any())).thenReturn("mock.jwt.token");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("mock.jwt.token", resp.getToken());
        assertEquals("Alice Chen", resp.getUser().getFullName());
        assertTrue(resp.getUser().getRoles().contains("ROLE_STUDENT"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateEmail_ThrowsBadRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setStudentId("STU9999");
        req.setEmail("duplicate@univ.edu");
        req.setPassword("Password123!");
        req.setFullName("Bob");

        when(userRepository.existsByEmail("duplicate@univ.edu")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_Success() {
        LoginRequest req = new LoginRequest();
        req.setEmailOrStudentId("teststudent@univ.edu");
        req.setPassword("Password123!");

        User user = new User();
        user.setId(10L);
        user.setEmail("teststudent@univ.edu");
        user.setFullName("Alice Chen");
        user.setRoles(Collections.singleton(new Role(RoleName.ROLE_STUDENT)));

        when(userRepository.findByEmail("teststudent@univ.edu")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(any())).thenReturn("jwt.token.abc");

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("jwt.token.abc", resp.getToken());
        assertEquals("Alice Chen", resp.getUser().getFullName());
    }
}
