package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import java.util.List;
import java.util.UUID;

public interface CareerService {

    // --- Education ---
    EducationResponse createEducation(String email, EducationRequest request);
    List<EducationResponse> getEducations(String email);
    EducationResponse getEducation(String email, UUID educationId);
    EducationResponse updateEducation(String email, UUID educationId, EducationRequest request);
    void deleteEducation(String email, UUID educationId);

    // --- Experience ---
    ExperienceResponse createExperience(String email, ExperienceRequest request);
    List<ExperienceResponse> getExperiences(String email);
    ExperienceResponse getExperience(String email, UUID experienceId);
    ExperienceResponse updateExperience(String email, UUID experienceId, ExperienceRequest request);
    void deleteExperience(String email, UUID experienceId);

    // --- User Skills ---
    List<UserSkillResponse> addUserSkills(String email, List<UserSkillRequest> requests);
    List<UserSkillResponse> getUserSkills(String email);
    List<UserSkillResponse> replaceUserSkills(String email, List<UserSkillRequest> requests);
    void removeUserSkill(String email, UUID skillId);

    // --- Master Skills ---
    List<SkillResponse> getAllMasterSkills();
}
