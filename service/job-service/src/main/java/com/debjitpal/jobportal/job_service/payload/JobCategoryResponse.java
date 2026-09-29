package com.debjitpal.jobportal.job_service.payload;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobCategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String iconUrl;
    private UUID parentId;
    private String parentName;
    private List<JobCategoryResponse> subCategories;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
