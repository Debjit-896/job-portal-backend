package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserSkillRequest {
    @NotNull(message = "Skill ID is required")
    private UUID skillId;
    private String proficiency;
    private BigDecimal yearsOfExperience;
}
