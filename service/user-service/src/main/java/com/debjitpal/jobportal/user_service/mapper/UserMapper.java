package com.debjitpal.jobportal.user_service.mapper;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.entity.User;

public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setStatus(user.getStatus());
        userResponse.setLastLoginAt(user.getLastLoginAt());
        userResponse.setCreatedAt(user.getCreatedAt());
        return userResponse;
    }
}
