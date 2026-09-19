package com.debjitpal.jobportal.user_service.dto;

import com.debjitpal.jobportal.dto.response.UserResponse;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String title;
    private String message;
    private UserResponse userResponse;
}
