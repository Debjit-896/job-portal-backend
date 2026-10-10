package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.domain.UserRole;
import com.debjitpal.jobportal.domain.UserStatus;
import com.debjitpal.jobportal.user_service.dto.request.SignupRequest;
import com.debjitpal.jobportal.user_service.dto.response.AuthResponse;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.entity.UserProfile;
import com.debjitpal.jobportal.user_service.repository.UserProfileRepository;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import com.debjitpal.jobportal.user_service.security.JwtProvider;
import com.debjitpal.jobportal.user_service.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("encodedPassword")
                .name("Test User")
                .role(UserRole.JOB_SEEKER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .build();
    }

    @Test
    void signup_ShouldCreateUserAndReturnAuthResponse() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setPassword("password");
        request.setName("New User");
        request.setRole(UserRole.JOB_SEEKER);

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtProvider.generateToken(any(Authentication.class), any(UUID.class))).thenReturn("access-token");
        when(tokenService.generateRefreshToken(any(User.class), anyString(), anyString(), anyString())).thenReturn("refresh-token");

        AuthResponse response = authService.signup(request);

        assertNotNull(response);
        assertEquals("access-token", response.getJwt());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void signup_EmailAlreadyExists_ShouldThrowException() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@test.com");
        
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        
        assertThrows(RuntimeException.class, () -> authService.signup(request));
    }
}
