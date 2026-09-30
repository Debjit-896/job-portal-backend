package com.debjitpal.jobportal.job_service.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobCategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String iconUrl;
    private Long parentId;
    private String parentName;
    private List<JobCategoryResponse> subCategories;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
