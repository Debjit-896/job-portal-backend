package com.debjitpal.jobportal.dto.response;

import com.debjitpal.jobportal.domain.ExperienceLevel;
import com.debjitpal.jobportal.domain.JobStatus;
import com.debjitpal.jobportal.domain.JobType;
import com.debjitpal.jobportal.domain.WorkMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse{
    private UUID id;
    private Long employerId;
    private String title;
    private String description;
    private String requirements;
    private String responsibilities;
    private String benefits;
    private String city;
    private String state;
    private String country;
    private String pinCode;
    private String address;
    private CompanyResponse company;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private JobType jobType;
    private WorkMode workMode;
    private JobStatus jobStatus;
    private ExperienceLevel experienceLevel;
    private Integer openings;
    private LocalDate applicationDeadline;
    private LocalDate expiredAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;
}