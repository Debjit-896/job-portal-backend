package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserSkillRequest {
    private UUID skillId;
    private String proficiency;
    private BigDecimal yearsOfExperience;
}
