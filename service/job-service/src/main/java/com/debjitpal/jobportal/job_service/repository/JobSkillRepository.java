package com.debjitpal.jobportal.job_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.debjitpal.jobportal.job_service.model.JobSkill;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {
    boolean existsBySlug(String slug);
    boolean existsByName(String name);

    List<JobSkill> findByIsActiveTrue();
}
