package com.debjitpal.jobportal.job_service.payload;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.debjitpal.jobportal.domain.ExperienceLevel;
import com.debjitpal.jobportal.domain.JobStatus;
import com.debjitpal.jobportal.domain.JobType;
import com.debjitpal.jobportal.domain.WorkMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchRequest {
    private String keyword;
    private Long categoryId;
    private List<Long> skillIds;
    private List<Long> tagIds;
    private UUID companyId;
    private String location;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private JobType jobType;
    private WorkMode workMode;
    private ExperienceLevel experienceLevel;
    private JobStatus jobStatus;
    private Integer minOpenings;
    private Integer maxOpenings;
}
