package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = User.builder().id(UUID.randomUUID()).email("test@example.com").build();
    }

    @Test
    void generateRefreshToken_ShouldSaveAndReturnToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        String refreshToken = tokenService.generateRefreshToken(testUser, "Device", "127.0.0.1", "Browser");
        assertNotNull(refreshToken);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }
}
