package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserProfileRequest;
import com.debjitpal.jobportal.user_service.dto.response.UserProfileResponse;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.entity.UserProfile;
import com.debjitpal.jobportal.user_service.repository.RefreshTokenRepository;
import com.debjitpal.jobportal.user_service.repository.UserProfileRepository;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserServiceImple userService;

    private User testUser;
    private UserProfile testProfile;
    private final UUID userId = UUID.randomUUID();
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(userId)
                .email(email)
                .name("Test User")
                .emailVerified(true)
                .build();
                
        testProfile = UserProfile.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .firstName("Test")
                .lastName("User")
                .headline("Software Engineer")
                .build();
    }

    @Test
    void getMyAccount_ShouldReturnUserResponse() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        
        UserResponse response = userService.getMyAccount(email);
        
        assertNotNull(response);
        assertEquals(email, response.getEmail());
        assertEquals("Test User", response.getName());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getMyAccount_UserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getMyAccount(email));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void getMyProfile_ShouldReturnProfileResponse() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(testProfile));
        
        UserProfileResponse response = userService.getMyProfile(email);
        
        assertNotNull(response);
        assertEquals("Test", response.getFirstName());
        assertEquals("Software Engineer", response.getHeadline());
    }

    @Test
    void getMyProfile_NoProfileExists_ShouldReturnEmptyProfileResponse() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        UserProfileResponse response = userService.getMyProfile(email);
        
        assertNotNull(response);
        assertNull(response.getFirstName()); // Should be mapped from empty builder
    }

    @Test
    void createOrReplaceMyProfile_ShouldSaveAndReturnProfile() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFirstName("John");
        request.setHeadline("Developer");
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArguments()[0]);
        
        UserProfileResponse response = userService.createOrReplaceMyProfile(email, request);
        
        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Developer", response.getHeadline());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }
}
