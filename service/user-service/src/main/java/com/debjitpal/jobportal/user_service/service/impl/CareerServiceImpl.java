package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.user_service.entity.*;
import com.debjitpal.jobportal.user_service.repository.*;
import com.debjitpal.jobportal.user_service.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // --- Education ---

    private EducationResponse mapEducation(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .institution(education.getInstitution())
                .degree(education.getDegree())
                .fieldOfStudy(education.getFieldOfStudy())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .grade(education.getGrade())
                .gradeType(education.getGradeType())
                .description(education.getDescription())
                .build();
    }

    @Override
    @Transactional
    public EducationResponse createEducation(String email, EducationRequest request) {
        User user = getUserByEmail(email);
        Education education = Education.builder()
                .user(user)
                .institution(request.getInstitution())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .grade(request.getGrade())
                .gradeType(request.getGradeType())
                .description(request.getDescription())
                .build();
        return mapEducation(educationRepository.save(education));
    }

    @Override
    public List<EducationResponse> getEducations(String email) {
        User user = getUserByEmail(email);
        return educationRepository.findByUserId(user.getId()).stream()
                .map(this::mapEducation).collect(Collectors.toList());
    }

    @Override
    public EducationResponse getEducation(String email, UUID educationId) {
        User user = getUserByEmail(email);
        Education education = educationRepository.findById(educationId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Education not found"));
        return mapEducation(education);
    }

    @Override
    @Transactional
    public EducationResponse updateEducation(String email, UUID educationId, EducationRequest request) {
        User user = getUserByEmail(email);
        Education education = educationRepository.findById(educationId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Education not found"));
        
        education.setInstitution(request.getInstitution());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setGrade(request.getGrade());
        education.setGradeType(request.getGradeType());
        education.setDescription(request.getDescription());
        
        return mapEducation(educationRepository.save(education));
    }

    @Override
    @Transactional
    public void deleteEducation(String email, UUID educationId) {
        User user = getUserByEmail(email);
        Education education = educationRepository.findById(educationId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Education not found"));
        educationRepository.delete(education);
    }

    // --- Experience ---

    private ExperienceResponse mapExperience(Experience experience) {
        return ExperienceResponse.builder()
                .id(experience.getId())
                .company(experience.getCompany())
                .jobTitle(experience.getJobTitle())
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .currentlyWorking(experience.getCurrentlyWorking())
                .description(experience.getDescription())
                .location(experience.getLocation())
                .build();
    }

    @Override
    @Transactional
    public ExperienceResponse createExperience(String email, ExperienceRequest request) {
        User user = getUserByEmail(email);
        Experience experience = Experience.builder()
                .user(user)
                .company(request.getCompany())
                .jobTitle(request.getJobTitle())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currentlyWorking(request.getCurrentlyWorking())
                .description(request.getDescription())
                .location(request.getLocation())
                .build();
        return mapExperience(experienceRepository.save(experience));
    }

    @Override
    public List<ExperienceResponse> getExperiences(String email) {
        User user = getUserByEmail(email);
        return experienceRepository.findByUserId(user.getId()).stream()
                .map(this::mapExperience).collect(Collectors.toList());
    }

    @Override
    public ExperienceResponse getExperience(String email, UUID experienceId) {
        User user = getUserByEmail(email);
        Experience experience = experienceRepository.findById(experienceId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        return mapExperience(experience);
    }

    @Override
    @Transactional
    public ExperienceResponse updateExperience(String email, UUID experienceId, ExperienceRequest request) {
        User user = getUserByEmail(email);
        Experience experience = experienceRepository.findById(experienceId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        experience.setCompany(request.getCompany());
        experience.setJobTitle(request.getJobTitle());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setCurrentlyWorking(request.getCurrentlyWorking());
        experience.setDescription(request.getDescription());
        experience.setLocation(request.getLocation());

        return mapExperience(experienceRepository.save(experience));
    }

    @Override
    @Transactional
    public void deleteExperience(String email, UUID experienceId) {
        User user = getUserByEmail(email);
        Experience experience = experienceRepository.findById(experienceId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        experienceRepository.delete(experience);
    }

    // --- Skills ---

    private SkillResponse mapSkill(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .build();
    }

    private UserSkillResponse mapUserSkill(UserSkill userSkill) {
        return UserSkillResponse.builder()
                .skill(mapSkill(userSkill.getSkill()))
                .proficiency(userSkill.getProficiency())
                .yearsOfExperience(userSkill.getYearsOfExperience())
                .build();
    }

    @Override
    public List<SkillResponse> getAllMasterSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapSkill).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserSkillResponse> addUserSkills(String email, List<UserSkillRequest> requests) {
        User user = getUserByEmail(email);
        for (UserSkillRequest req : requests) {
            Skill skill = skillRepository.findById(req.getSkillId())
                    .orElseThrow(() -> new RuntimeException("Skill not found in master list: " + req.getSkillId()));
            
            UserSkillId id = new UserSkillId(user.getId(), skill.getId());
            if (!userSkillRepository.existsById(id)) {
                UserSkill userSkill = UserSkill.builder()
                        .id(id)
                        .user(user)
                        .skill(skill)
                        .proficiency(req.getProficiency())
                        .yearsOfExperience(req.getYearsOfExperience())
                        .build();
                userSkillRepository.save(userSkill);
            }
        }
        return getUserSkills(email);
    }

    @Override
    public List<UserSkillResponse> getUserSkills(String email) {
        User user = getUserByEmail(email);
        return userSkillRepository.findByIdUserId(user.getId()).stream()
                .map(this::mapUserSkill).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserSkillResponse> replaceUserSkills(String email, List<UserSkillRequest> requests) {
        User user = getUserByEmail(email);
        userSkillRepository.deleteByIdUserId(user.getId());
        userSkillRepository.flush();
        return addUserSkills(email, requests);
    }

    @Override
    @Transactional
    public void removeUserSkill(String email, UUID skillId) {
        User user = getUserByEmail(email);
        UserSkillId id = new UserSkillId(user.getId(), skillId);
        userSkillRepository.findById(id).ifPresent(userSkillRepository::delete);
    }
}
