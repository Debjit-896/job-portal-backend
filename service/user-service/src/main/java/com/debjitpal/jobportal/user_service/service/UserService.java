package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.UpdateUserRequest;
import com.debjitpal.jobportal.user_service.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // USER
    User getUserByEmail(String email);
    User getUserById(UUID id);
    List<User> getAllUsers();
    UserResponse updateProfile(String email, UpdateUserRequest request);

    // ADMIN
    UserResponse suspendUser(UUID id);
    UserResponse activateUser(UUID id);
    UserResponse deleteUser(UUID id);
}
