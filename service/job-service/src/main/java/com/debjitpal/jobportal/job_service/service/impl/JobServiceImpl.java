package com.debjitpal.jobportal.job_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.debjitpal.jobportal.domain.JobStatus;
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
    public JobResponse createJob(UUID employerId, JobRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Job request is required");
        }

        if (employerId == null) {
            throw new IllegalArgumentException("Employer id is required");
        }

        Job job = Job.builder()
                .companyId(UUID.randomUUID()) // TODO: Resolve actual companyId from employer
                .employerId(employerId)
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
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found!"));
        assertEmployer(job, employerId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setResponsibilities(request.getResponsibilities());
        job.setBenefits(request.getBenefits());
        job.setLocation(buildLocation(request));
        job.setSalaryRange(buildSalaryRange(request));
        job.setJobType(request.getJobType());
        job.setWorkMode(request.getWorkMode());
        job.setJobStatus(request.getJobStatus());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setOpenings(request.getOpenings());
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setExpiredAt(request.getExpiredAt());
        job.setIsActive(request.getIsActive());

        return convertToResponse(jobRepository.save(job));
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
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not fund!"));
        assertEmployer(job, employerId);

        if (job.getJobStatus()==JobStatus.CLOSED || job.getJobStatus()==JobStatus.EXPIRED){
            throw new RuntimeException("Job is expired!");
        }

        job.setJobStatus(JobStatus.OPEN);
        job.setPublishedAt(LocalDateTime.now());
        job.setIsActive(true);
        return convertToResponse(jobRepository.save(job));
    }

    private void assertEmployer(Job job, UUID employerId) {
        if (!job.getEmployerId().equals(employerId)){
            throw new RuntimeException("You are not the eployer who posted this job!");
        }
    }

    @Override
    public JobResponse closeJob(UUID jobId, UUID employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not fund!"));
        assertEmployer(job, employerId);

        job.setJobStatus(JobStatus.CLOSED);
        job.setClosedAt(LocalDateTime.now());
        job.setIsActive(false);
        return convertToResponse(jobRepository.save(job));
    }

    @Override
    public void deleteJob(UUID jobId, UUID employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not fund!"));
        assertEmployer(job, employerId);
        assertEmployer(job, employerId);
        jobRepository.delete(job);
    }

    @Override
    public List<JobResponse> getAllJobsAdmin() {
        return jobRepository.findAll().stream().map(
                this::convertToResponse
        ).collect(Collectors.toList());
    }
}
