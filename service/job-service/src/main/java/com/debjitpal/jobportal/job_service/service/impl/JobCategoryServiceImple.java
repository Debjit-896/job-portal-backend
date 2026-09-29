package com.debjitpal.jobportal.job_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.debjitpal.jobportal.job_service.mapper.JobCategoryMapper;
import com.debjitpal.jobportal.job_service.model.JobCategory;
import com.debjitpal.jobportal.job_service.payload.JobCategoryRequest;
import com.debjitpal.jobportal.job_service.payload.JobCategoryResponse;
import com.debjitpal.jobportal.job_service.repository.JobCategoryRepository;
import com.debjitpal.jobportal.job_service.service.JobCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImple implements JobCategoryService {

    private final JobCategoryRepository jobCategoryRepository;

    @Override
    public JobCategoryResponse createCategory(JobCategoryRequest request) {
        if (jobCategoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists");
        }

        JobCategory parent = null;

        if (request.getParentId() != null) {
            parent = getCategoryEntityById(request.getParentId());
        }

        String slug = generateUniqueSlug(request.getName());

        JobCategory category = new JobCategory();
        category.setName(request.getName());
        category.setSlug(slug);
        category.setParent(parent);
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());

        JobCategory savedCategory = jobCategoryRepository.save(category);
        return JobCategoryMapper.toJobCategoryResponse(savedCategory, false);
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9\\s-]+", "")
                .trim().replaceAll("[\\s-]+", "-");

        if (!jobCategoryRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        int counter = 1;

        while (jobCategoryRepository.existsBySlug(baseSlug + "-" + counter)) {
            counter++;
        }

        return baseSlug + "-" + counter;
    }

    @Override
    public JobCategory getCategoryEntityById(Long id) {
        return jobCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public List<JobCategoryResponse> getAllCategories() {
        return jobCategoryRepository.findByIsActiveTrue().stream()
                .map(c -> JobCategoryMapper.toJobCategoryResponse(c, false))
                .collect(Collectors.toList());
    }

    @Override
    public JobCategoryResponse getCategoryById(Long id) {
        JobCategory jobCategory = getCategoryEntityById(id);
        return JobCategoryMapper.toJobCategoryResponse(jobCategory, true);
    }

    @Override
    public JobCategoryResponse updateCategory(Long id, JobCategoryRequest request) {
        JobCategory category = getCategoryEntityById(id);

        if (!category.getName().equals(request.getName()) && jobCategoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists");
        }

        JobCategory parent = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new RuntimeException("Parent category cannot be the same as the category");
            }
            parent = getCategoryEntityById(request.getParentId());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());
        category.setParent(parent);

        JobCategory updatedCategory = jobCategoryRepository.save(category);
        return JobCategoryMapper.toJobCategoryResponse(updatedCategory, false);
    }

    @Override
    public void deleteCategory(Long id) {
        JobCategory jobCategory = getCategoryEntityById(id);
        jobCategory.setIsActive(false);
        jobCategoryRepository.save(jobCategory);
    }
}