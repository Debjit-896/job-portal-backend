package com.debjitpal.jobportal.job_service.repository;

import com.debjitpal.jobportal.job_service.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {
     List<Job> findByCompanyId(UUID companyId);
}
