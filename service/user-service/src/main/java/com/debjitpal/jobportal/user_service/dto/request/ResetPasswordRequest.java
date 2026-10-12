package com.debjitpal.jobportal.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String token;

    private String email;

    private String otp;

    @NotBlank(message = "New password is required")
    private String newPassword;

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }
}
