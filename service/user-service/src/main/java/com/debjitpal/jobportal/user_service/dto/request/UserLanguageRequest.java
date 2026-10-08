package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class UserLanguageRequest {
    private UUID languageId;
    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED, NATIVE
}
