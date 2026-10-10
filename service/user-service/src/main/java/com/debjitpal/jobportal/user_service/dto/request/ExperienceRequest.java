package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class ExperienceRequest {
    @NotBlank(message = "Company is required")
    private String company;
    
    @NotBlank(message = "Job title is required")
    private String jobTitle;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
    private String description;
    private String location;
}
