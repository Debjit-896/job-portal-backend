package com.debjitpal.jobportal.job_service.payload;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    @Size(max = 500,message = "Description must be less than 500 characters")
    private String description;
    
    private String iconUrl;
    
    private Long parentId;
    
    private String parentName;
}
