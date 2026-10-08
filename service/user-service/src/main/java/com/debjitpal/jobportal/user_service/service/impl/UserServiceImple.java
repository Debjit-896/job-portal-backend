package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.domain.UserStatus;
import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserRequest;
import com.debjitpal.jobportal.user_service.dto.response.UserProfileResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserProfileRequest;
import com.debjitpal.jobportal.user_service.mapper.UserMapper;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.entity.UserProfile;
import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.user_service.repository.RefreshTokenRepository;
import com.debjitpal.jobportal.user_service.dto.response.SessionResponse;
import com.debjitpal.jobportal.user_service.dto.response.AccountStatusResponse;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import com.debjitpal.jobportal.user_service.repository.UserProfileRepository;
import com.debjitpal.jobportal.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse getMyAccount(String email) {
        return UserMapper.toUserResponse(getUserByEmail(email));
    }

    @Override
    public UserResponse updateMyAccount(String email, UpdateUserRequest request) {
        User user = getUserByEmail(email);
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void requestAccountDeletion(String email) {
        User user = getUserByEmail(email);
        // Note: For a real deletion workflow, consider foreign keys, retention, and cascade logic.
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public AccountStatusResponse getAccountStatus(String email) {
        User user = getUserByEmail(email);
        return AccountStatusResponse.builder()
                .status(user.getStatus().name())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    @Override
    public List<SessionResponse> getActiveSessions(String email) {
        User user = getUserByEmail(email);
        return refreshTokenRepository.findByUser(user).stream()
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(rt -> SessionResponse.builder()
                        .sessionId(rt.getId())
                        .deviceInfo(rt.getDeviceInfo())
                        .ipAddress(rt.getIpAddress())
                        .userAgent(rt.getUserAgent())
                        .createdAt(rt.getCreatedAt())
                        .expiresAt(rt.getExpiresAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeSession(String email, UUID sessionId) {
        User user = getUserByEmail(email);
        refreshTokenRepository.findById(sessionId).ifPresent(rt -> {
            if (rt.getUser().getId().equals(user.getId()) && !rt.isRevoked()) {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            }
        });
    }

    @Override
    @Transactional
    public void revokeAllSessions(String email) {
        User user = getUserByEmail(email);
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUser(user).stream()
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
                
        activeTokens.forEach(rt -> rt.setRevoked(true));
        refreshTokenRepository.saveAll(activeTokens);
    }

    private UserProfileResponse mapToProfileResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .username(profile.getUsername())
                .headline(profile.getHeadline())
                .bio(profile.getBio())
                .phone(profile.getPhone())
                .profilePicture(profile.getProfilePicture())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .website(profile.getWebsite())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .build();
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(UserProfile.builder().user(user).build());
        return mapToProfileResponse(profile);
    }

    @Override
    public UserProfileResponse getPublicProfile(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        return mapToProfileResponse(profile);
    }

    @Override
    public UserProfileResponse createOrReplaceMyProfile(String email, UpdateUserProfileRequest request) {
        User user = getUserByEmail(email);
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(UserProfile.builder().user(user).build());
        
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setUsername(request.getUsername());
        profile.setHeadline(request.getHeadline());
        profile.setBio(request.getBio());
        profile.setPhone(request.getPhone());
        profile.setProfilePicture(request.getProfilePicture());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setWebsite(request.getWebsite());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
        
        return mapToProfileResponse(userProfileRepository.save(profile));
    }

    @Override
    public UserProfileResponse partiallyUpdateMyProfile(String email, UpdateUserProfileRequest request) {
        User user = getUserByEmail(email);
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(UserProfile.builder().user(user).build());
                
        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getUsername() != null) profile.setUsername(request.getUsername());
        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getProfilePicture() != null) profile.setProfilePicture(request.getProfilePicture());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        
        return mapToProfileResponse(userProfileRepository.save(profile));
    }

    @Override
    public UserResponse suspendUser(UUID id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.SUSPENDED);
        user.setSuspendedAt(LocalDateTime.now());
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse activateUser(UUID id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setSuspendedAt(null);
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deleteUser(UUID id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        return UserMapper.toUserResponse(userRepository.save(user));
    }
}
