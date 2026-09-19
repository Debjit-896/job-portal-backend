package com.debjitpal.jobportal.dto.response;

import com.debjitpal.jobportal.domain.UserRole;
import com.debjitpal.jobportal.domain.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String profileImage;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

}
