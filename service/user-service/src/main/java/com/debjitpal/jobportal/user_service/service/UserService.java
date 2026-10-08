package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserRequest;
import com.debjitpal.jobportal.user_service.dto.response.UserProfileResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserProfileRequest;
import com.debjitpal.jobportal.user_service.dto.response.AccountStatusResponse;
import com.debjitpal.jobportal.user_service.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // USER
    User getUserByEmail(String email);
    User getUserById(UUID id);
    List<User> getAllUsers();
    
    // Account details
    UserResponse getMyAccount(String email);
    UserResponse updateMyAccount(String email, UpdateUserRequest request);
    void requestAccountDeletion(String email);
    AccountStatusResponse getAccountStatus(String email);

    // Sessions
    List<com.debjitpal.jobportal.user_service.dto.response.SessionResponse> getActiveSessions(String email);
    void revokeSession(String email, UUID sessionId);
    void revokeAllSessions(String email);

    // Profile details
    UserProfileResponse getMyProfile(String email);
    UserProfileResponse getPublicProfile(UUID userId);
    UserProfileResponse createOrReplaceMyProfile(String email, UpdateUserProfileRequest request);
    UserProfileResponse partiallyUpdateMyProfile(String email, UpdateUserProfileRequest request);

    // ADMIN
    UserResponse suspendUser(UUID id);
    UserResponse activateUser(UUID id);
    UserResponse deleteUser(UUID id);
}
