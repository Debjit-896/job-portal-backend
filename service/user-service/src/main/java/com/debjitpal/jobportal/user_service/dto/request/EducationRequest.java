package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class EducationRequest {
    @NotBlank(message = "Institution is required")
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String grade;
    private String gradeType;
    private String description;
}
