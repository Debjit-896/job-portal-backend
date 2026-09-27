package com.debjitpal.jobportal.job_service.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.debjitpal.jobportal.dto.request.JobRequest;
import com.debjitpal.jobportal.dto.response.CompanyResponse;
import com.debjitpal.jobportal.dto.response.JobResponse;
import com.debjitpal.jobportal.job_service.mapper.JobMapper;
import com.debjitpal.jobportal.job_service.model.Job;
import com.debjitpal.jobportal.job_service.model.Location;
import com.debjitpal.jobportal.job_service.model.SalaryRange;
import com.debjitpal.jobportal.job_service.payload.JobSearchRequest;
import com.debjitpal.jobportal.job_service.repository.JobRepository;
import com.debjitpal.jobportal.job_service.repository.JobSpecification;
import com.debjitpal.jobportal.job_service.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    public JobResponse createJob(Long employerId, JobRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Job request is required");
        }

        if (employerId == null) {
            throw new IllegalArgumentException("Employer id is required");
        }

        Job job = Job.builder()
                .companyId(UUID.randomUUID())
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .responsibilities(request.getResponsibilities())
                .benefits(request.getBenefits())
                .location(buildLocation(request))
                .salaryRange(buildSalaryRange(request))
                .jobType(request.getJobType())
                .workMode(request.getWorkMode())
                .jobStatus(request.getJobStatus() != null ? request.getJobStatus() : com.debjitpal.jobportal.domain.JobStatus.DRAFT)
                .experienceLevel(request.getExperienceLevel())
                .openings(request.getOpenings() != null ? request.getOpenings() : 1)
                .applicationDeadline(request.getApplicationDeadline())
                .expiredAt(request.getExpiredAt())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Job savedJob = jobRepository.save(job);
        return convertToResponse(savedJob);
    }

    private JobResponse convertToResponse(Job savedJob) {
        // TODO: Fetch company Response
        CompanyResponse companyResponse = CompanyResponse.builder()
                .id(savedJob.getCompanyId())
                .build();
        return JobMapper.toResponse(savedJob, companyResponse);
    }

    private SalaryRange buildSalaryRange(JobRequest request) {
        return SalaryRange.builder()
                .minimumSalary(request.getMinSalary())
                .maximumSalary(request.getMaxSalary())
                .build();
    }

    private Location buildLocation(JobRequest request) {
        return Location.builder()
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .pinCode(request.getPinCode())
                .address(request.getAddress())
                .build();
    }

    @Override
    public JobResponse getJobById(UUID jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found!"));
        return convertToResponse(job);
    }

    @Override
    public List<JobResponse> getJobs(JobSearchRequest request) {
        List<Job> jobs = jobRepository.findAll(JobSpecification.build(request));
        return jobs.stream().map(
                this::convertToResponse
        ).collect(Collectors.toList());
    }

    @Override
    public JobResponse updateJob(UUID jobId, UUID employerId, JobRequest request) {
        return null;
    }

    @Override
    public List<JobResponse> getJobByComapnyId(UUID companyId) {
        List<Job> jobs = jobRepository.findByCompanyId(companyId);
        return jobs.stream().map(
                this::convertToResponse
        ).collect(Collectors.toList());
    }

    @Override
    public JobResponse publishJob(UUID jobId, UUID employerId) {
        return null;
    }

    @Override
    public JobResponse closeJob(UUID jobId, UUID employerId) {
        return null;
    }

    @Override
    public JobResponse deleteJob(UUID jobId, UUID employerId) {
        return null;
    }

    @Override
    public List<JobResponse> getAllJobsAdmin() {
        return List.of();
    }
}
