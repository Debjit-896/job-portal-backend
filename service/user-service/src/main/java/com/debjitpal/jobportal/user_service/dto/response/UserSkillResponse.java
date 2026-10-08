package com.debjitpal.jobportal.user_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserSkillResponse {
    private SkillResponse skill;
    private String proficiency;
    private BigDecimal yearsOfExperience;
}
