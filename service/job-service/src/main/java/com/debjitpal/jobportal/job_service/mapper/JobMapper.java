package com.debjitpal.jobportal.job_service.mapper;

import com.debjitpal.jobportal.dto.response.CompanyResponse;
import com.debjitpal.jobportal.dto.response.JobResponse;
import com.debjitpal.jobportal.job_service.model.Job;
import com.debjitpal.jobportal.job_service.model.Location;
import com.debjitpal.jobportal.job_service.model.SalaryRange;

public class JobMapper {

    public static JobResponse toResponse(Job job, CompanyResponse companyResponse) {
        Location location=job.getLocation();
        SalaryRange salaryRange=job.getSalaryRange();
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .responsibilities(job.getResponsibilities())
                .benefits(job.getBenefits())
                .company(companyResponse)
                .address(location != null ? location.getAddress() : null)
                .city(location != null ? location.getCity() : null)
                .state(location != null ? location.getState() : null)
                .country(location != null ? location.getCountry() : null)
                .pinCode(location != null ? location.getPinCode() : null)
                .minSalary(location != null ? salaryRange.getMinimumSalary() : null)
                .maxSalary(location != null ? salaryRange.getMaximumSalary() : null)
                .jobType(job.getJobType())
                .workMode(job.getWorkMode())
                .experienceLevel(job.getExperienceLevel())
                .jobStatus(job.getJobStatus())
                .openings(job.getOpenings())
                .applicationDeadline(job.getApplicationDeadline())
                .expiredAt(job.getExpiredAt())
                .isActive(job.getIsActive())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .publishedAt(job.getPublishedAt())
                .closedAt(job.getClosedAt())
                .build();
    }
}
