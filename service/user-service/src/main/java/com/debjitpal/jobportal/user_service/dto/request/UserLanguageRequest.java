package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UserLanguageRequest {
    @NotNull(message = "Language ID is required")
    private UUID languageId;
    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED, NATIVE
}
