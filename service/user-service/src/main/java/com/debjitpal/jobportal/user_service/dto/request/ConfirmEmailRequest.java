package com.debjitpal.jobportal.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmEmailRequest {
    @NotBlank(message = "Token is required")
    private String token;
}
