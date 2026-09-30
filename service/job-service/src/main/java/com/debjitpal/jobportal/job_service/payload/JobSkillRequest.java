package com.debjitpal.jobportal.job_service.payload;

import com.debjitpal.jobportal.domain.SkillCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder
public class JobSkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max=100, message = "Skill name cannot be more than 100 characters")
    private String name;
    
    @NotNull(message = "Skill category is required")
    private SkillCategory category;
}
