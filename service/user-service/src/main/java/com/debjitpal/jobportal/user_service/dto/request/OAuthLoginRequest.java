package com.debjitpal.jobportal.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
