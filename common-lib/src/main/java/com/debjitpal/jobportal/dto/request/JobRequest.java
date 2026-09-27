package com.debjitpal.jobportal.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.debjitpal.jobportal.domain.ExperienceLevel;
import com.debjitpal.jobportal.domain.JobStatus;
import com.debjitpal.jobportal.domain.JobType;
import com.debjitpal.jobportal.domain.WorkMode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    private String requirements;
    private String responsibilities;
    private String benefits;

    private String city;
    private String state;
    private String country;
    private String pinCode;
    private String address;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Minimum salary must be greater than or equal to 0"
    )
    private BigDecimal minSalary;

    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Maximum salary must be greater than or equal to 0"
    )
    private BigDecimal maxSalary;

    private Set<Long> tagIds;
    private Set<Long> skillIds;

    private JobType jobType;
    private WorkMode workMode;
    private JobStatus jobStatus;
    private ExperienceLevel experienceLevel;

    @Min(value = 1, message = "Openings must be at least 1")
    private Integer openings;

    private LocalDate applicationDeadline;
    private LocalDate expiredAt;

    private Boolean isActive;
}