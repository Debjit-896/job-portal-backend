package com.debjitpal.jobportal.job_service.repository;

import com.debjitpal.jobportal.job_service.model.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
  boolean existsByName(String name);

  boolean existsBySlug(String slug);

  List<JobCategory> findByIsActiveTrue();

}
