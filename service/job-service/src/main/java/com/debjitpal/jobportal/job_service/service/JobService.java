package com.debjitpal.jobportal.job_service.service;

import com.debjitpal.jobportal.dto.request.JobRequest;
import com.debjitpal.jobportal.dto.response.JobResponse;
import com.debjitpal.jobportal.job_service.payload.JobSearchRequest;

import java.util.List;
import java.util.UUID;

public interface JobService {
    JobResponse createJob(UUID employerId, JobRequest request);

    JobResponse getJobById(UUID jobId);

    List<JobResponse> getJobs(JobSearchRequest request);

    JobResponse updateJob(UUID jobId, UUID employerId, JobRequest request);

    List<JobResponse> getJobByComapnyId(UUID companyId);

    JobResponse publishJob(UUID jobId, UUID employerId);

    JobResponse closeJob(UUID jobId, UUID employerId);

    void deleteJob(UUID jobId, UUID employerId);

    List<JobResponse> getAllJobsAdmin();
}