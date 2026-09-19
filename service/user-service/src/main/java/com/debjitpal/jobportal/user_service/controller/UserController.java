package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.UpdateUserRequest;
import com.debjitpal.jobportal.user_service.mapper.UserMapper;
import com.debjitpal.jobportal.user_service.model.User;
import com.debjitpal.jobportal.user_service.service.impl.UserServiceImple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImple userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            @RequestHeader("X-User-Email") String email) throws Exception {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader("X-User-Email") String email,
            @RequestBody UpdateUserRequest request) throws Exception {
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("id") UUID id) throws Exception {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() throws Exception {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(UserMapper::toUserResponse).toList());
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<UserResponse> suspendUser(
            @PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(
            @PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(
            @PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
