package com.debjitpal.jobportal.job_service.mapper;

import com.debjitpal.jobportal.dto.response.JobSkillResponse;
import com.debjitpal.jobportal.job_service.model.JobSkill;

public class JobSkillMapper {

    public static JobSkillResponse toJobSkillResponse(JobSkill skill) {
        return JobSkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .slug(skill.getSlug())
                .category(skill.getCategory())
                .isActive(skill.getIsActive())
                .build();
    }
}