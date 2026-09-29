package com.debjitpal.jobportal.job_service.repository;

import com.debjitpal.jobportal.job_service.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {
  List<Job> findByCompanyId(UUID companyId);
}