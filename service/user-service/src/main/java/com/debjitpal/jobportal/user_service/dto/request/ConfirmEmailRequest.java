package com.debjitpal.jobportal.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmEmailRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String otp;

    private String token;

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    public String getEffectiveOtp() {
        if (otp != null && !otp.isBlank()) {
            return otp.trim();
        }
        return token != null ? token.trim() : null;
    }
}
