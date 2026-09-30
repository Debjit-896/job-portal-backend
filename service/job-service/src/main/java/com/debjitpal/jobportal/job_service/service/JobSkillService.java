package com.debjitpal.jobportal.job_service.service;

import com.debjitpal.jobportal.dto.response.JobSkillResponse;
import com.debjitpal.jobportal.job_service.model.JobSkill;
import com.debjitpal.jobportal.job_service.payload.JobSkillRequest;

import java.util.List;
import java.util.Set;

public interface JobSkillService {
    JobSkillResponse createSkill(JobSkillRequest request);
    JobSkillResponse updateSkill(Long id, JobSkillRequest request);
    List<JobSkillResponse> getAllSkills();
    JobSkillResponse getSkillById(Long id);
    void deleteSkill(Long id);
    Set<JobSkill> getSkillEntitiesByIds(Set<Long> ids);
}