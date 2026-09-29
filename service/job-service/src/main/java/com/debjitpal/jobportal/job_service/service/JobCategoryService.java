package com.debjitpal.jobportal.job_service.service;

import java.util.List;

import com.debjitpal.jobportal.job_service.model.JobCategory;
import com.debjitpal.jobportal.job_service.payload.JobCategoryRequest;
import com.debjitpal.jobportal.job_service.payload.JobCategoryResponse;

public interface JobCategoryService {
    JobCategoryResponse createCategory(JobCategoryRequest request);

    List<JobCategoryResponse> getAllCategories();

    JobCategoryResponse getCategoryById(Long id);

    JobCategory getCategoryEntityById(Long id);

    JobCategoryResponse updateCategory(Long id, JobCategoryRequest request);

    void deleteCategory(Long id);
}