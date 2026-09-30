package com.debjitpal.jobportal.job_service.service.impl;

import com.debjitpal.jobportal.dto.response.JobSkillResponse;
import com.debjitpal.jobportal.job_service.mapper.JobSkillMapper;
import com.debjitpal.jobportal.job_service.model.JobSkill;
import com.debjitpal.jobportal.job_service.payload.JobSkillRequest;
import com.debjitpal.jobportal.job_service.repository.JobSkillRepository;
import com.debjitpal.jobportal.job_service.service.JobSkillService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobSkillServiceImpl implements JobSkillService {

    private final JobSkillRepository jobSkillRepository;

    @Override
    public JobSkillResponse createSkill(JobSkillRequest request) {
        if (jobSkillRepository.existsByName(request.getName())) {
            throw new RuntimeException("Skill already exists");
        }
        String slug = generateUniqueSlug(request.getName());
        
        JobSkill skill = JobSkill.builder()
                .name(request.getName())
                .slug(slug)
                .category(request.getCategory())
                .build();

        JobSkill savedSkill =jobSkillRepository.save(skill);
        return JobSkillMapper.toJobSkillResponse(savedSkill);
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9\\s-]+", "")
                .trim().replaceAll("[\\s-]+", "-");

        if (!jobSkillRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        int counter = 1;

        while (jobSkillRepository.existsBySlug(baseSlug + "-" + counter)) {
            counter++;
        }

        return baseSlug + "-" + counter;
    }

    @Override
    public List<JobSkillResponse> getAllSkills() {
        return jobSkillRepository.findByIsActiveTrue().stream()
                .map(JobSkillMapper::toJobSkillResponse)
                .toList();
    }

    @Override
    public JobSkillResponse getSkillById(Long id) {
        JobSkill jobSkill = getSkillEntityById(id);
        return JobSkillMapper.toJobSkillResponse(jobSkill);
    }

    public JobSkill getSkillEntityById(Long id) {
        return jobSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Skill not found with id: " + id));
    }

    @Override
    public Set<JobSkill> getSkillEntitiesByIds(Set<Long> ids) {
        Set<JobSkill> skills = new HashSet<>(jobSkillRepository.findAllById(ids));
        return skills;
    }

    @Override
    public JobSkillResponse updateSkill(Long id, JobSkillRequest request) {
        JobSkill jobSkill = jobSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Skill not found with id: " + id));

        if (!jobSkill.getName().equals(request.getName()) && jobSkillRepository.existsByName(request.getName())) {
            throw new RuntimeException("Skill already exists");
        }
        jobSkill.setName(request.getName());
        jobSkill.setCategory(request.getCategory());
        JobSkill updatedSkill = jobSkillRepository.save(jobSkill);
        return JobSkillMapper.toJobSkillResponse(updatedSkill);
    }

    @Override
    public void deleteSkill(Long id) {
        JobSkill jobSkill = jobSkillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Skill not found with id: " + id));
        jobSkill.setIsActive(false);
        jobSkillRepository.save(jobSkill);
    }
}