package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserPreferenceRequest {
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
