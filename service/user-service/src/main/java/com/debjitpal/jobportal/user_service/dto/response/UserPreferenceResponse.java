package com.debjitpal.jobportal.user_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UserPreferenceResponse {
    private UUID id;
    private String preferredJobTitles;
    private String preferredSkills;
    private BigDecimal expectedMinSalary;
    private BigDecimal expectedMaxSalary;
    private String preferredLocation;
    private String preferredJobType;
    private String preferredWorkMode;
    private String experienceLevel;
    private Boolean willingToRelocate;
    private Integer noticePeriodDays;
}
