package com.debjitpal.jobportal.job_service.mapper;

import com.debjitpal.jobportal.job_service.model.JobCategory;
import com.debjitpal.jobportal.job_service.payload.JobCategoryResponse;
import java.util.List;



public class JobCategoryMapper {

  public static JobCategoryResponse toJobCategoryResponse(
      JobCategory category, boolean includeChildren) {

    List<JobCategoryResponse> subCategories = null;
    if (includeChildren) {
      subCategories =
          category.getSubCategories().stream()
              .map(sub -> toJobCategoryResponse(sub, false))
              .toList();
    }

    return JobCategoryResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .slug(category.getSlug())
        .parentId(category.getParent() != null ? category.getParent().getId() : null)
        .description(category.getDescription())
        .iconUrl(category.getIconUrl())
        .isActive(category.getIsActive())
        .parentName(category.getParent() != null ? category.getParent().getName() : null)
        .createdAt(category.getCreatedAt() != null ? category.getCreatedAt().toString() : null)
        .subCategories(subCategories)
        .build();
  }
}
