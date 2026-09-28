package com.debjitpal.jobportal.job_service.controller;

import com.debjitpal.jobportal.dto.request.JobRequest;
import com.debjitpal.jobportal.dto.response.JobResponse;
import com.debjitpal.jobportal.job_service.payload.JobSearchRequest;
import com.debjitpal.jobportal.job_service.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;

  @PostMapping
  public ResponseEntity<JobResponse> createJob(
      @RequestBody @Valid JobRequest request, @RequestHeader("X-User-Id") UUID employerId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(jobService.createJob(employerId, request));
  }

  @GetMapping("/{jobId}")
  public ResponseEntity<JobResponse> getJobById(@PathVariable UUID jobId) {
    return ResponseEntity.status(HttpStatus.OK).body(jobService.getJobById(jobId));
  }

  @GetMapping
  public ResponseEntity<List<JobResponse>> getJobs(@ModelAttribute JobSearchRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(jobService.getJobs(request));
  }

  @PutMapping("/{jobId}")
  public ResponseEntity<JobResponse> updateJob(
      @PathVariable UUID jobId,
      @RequestBody @Valid JobRequest request,
      @RequestHeader("X-User-Id") UUID employerId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(jobService.updateJob(jobId, employerId, request));
  }

  @GetMapping("/company/{companyId}")
  public ResponseEntity<List<JobResponse>> getJobByComapnyId(@PathVariable UUID companyId) {
    return ResponseEntity.status(HttpStatus.OK).body(jobService.getJobByComapnyId(companyId));
  }

  @PatchMapping("/{jobId}/publish")
  public ResponseEntity<JobResponse> publishJob(
      @PathVariable UUID jobId, @RequestHeader("X-User-Id") UUID employerId) {
    return ResponseEntity.status(HttpStatus.OK).body(jobService.publishJob(jobId, employerId));
  }

  @PatchMapping("/{jobId}/close")
  public ResponseEntity<JobResponse> closeJob(
      @PathVariable UUID jobId, @RequestHeader("X-User-Id") UUID employerId) {
    return ResponseEntity.status(HttpStatus.OK).body(jobService.closeJob(jobId, employerId));
  }

  @GetMapping("/admin")
  public ResponseEntity<List<JobResponse>> getAllJobsAdmin() {
    return ResponseEntity.status(HttpStatus.OK).body(jobService.getAllJobsAdmin());
  }

  @DeleteMapping("/{jobId}")
  public ResponseEntity<Void> deleteJob(@PathVariable UUID jobId, @RequestHeader("X-User-Id") UUID employerId) {
    jobService.deleteJob(jobId, employerId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
