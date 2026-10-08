package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserRequest;
import com.debjitpal.jobportal.user_service.dto.response.UserProfileResponse;
import com.debjitpal.jobportal.user_service.dto.request.UpdateUserProfileRequest;
import com.debjitpal.jobportal.user_service.mapper.UserMapper;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- My Account APIs ---

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyAccount(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.getMyAccount(principal.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyAccount(
            Principal principal,
            @RequestBody UpdateUserRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.updateMyAccount(principal.getName(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> requestAccountDeletion(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        userService.requestAccountDeletion(principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/account-status")
    public ResponseEntity<com.debjitpal.jobportal.user_service.dto.response.AccountStatusResponse> getAccountStatus(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.getAccountStatus(principal.getName()));
    }

    // --- Session APIs ---

    @GetMapping("/me/sessions")
    public ResponseEntity<List<com.debjitpal.jobportal.user_service.dto.response.SessionResponse>> getActiveSessions(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.getActiveSessions(principal.getName()));
    }

    @DeleteMapping("/me/sessions/{sessionId}")
    public ResponseEntity<Void> revokeSession(Principal principal, @PathVariable("sessionId") UUID sessionId) {
        if (principal == null) return ResponseEntity.status(401).build();
        userService.revokeSession(principal.getName(), sessionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/sessions")
    public ResponseEntity<Void> revokeAllSessions(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        userService.revokeAllSessions(principal.getName());
        return ResponseEntity.ok().build();
    }

    // --- My Profile APIs ---

    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.getMyProfile(principal.getName()));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> createOrReplaceMyProfile(
            Principal principal,
            @RequestBody UpdateUserProfileRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.createOrReplaceMyProfile(principal.getName(), request));
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> partiallyUpdateMyProfile(
            Principal principal,
            @RequestBody UpdateUserProfileRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.partiallyUpdateMyProfile(principal.getName(), request));
    }

    // --- Public Profile API ---

    @GetMapping("/{userId}/public-profile")
    public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userService.getPublicProfile(userId));
    }

    // --- Admin APIs ---

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(UserMapper::toUserResponse).toList());
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<UserResponse> suspendUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
