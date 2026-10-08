package com.debjitpal.jobportal.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthLoginRequest {
    @NotBlank
    private String provider;

    @NotBlank
    private String providerUserId;

    @NotBlank
    private String email;

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    private String name;
}
