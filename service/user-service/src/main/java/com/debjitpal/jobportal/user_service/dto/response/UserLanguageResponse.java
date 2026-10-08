package com.debjitpal.jobportal.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLanguageResponse {
    private LanguageResponse language;
    private String proficiency;
}
