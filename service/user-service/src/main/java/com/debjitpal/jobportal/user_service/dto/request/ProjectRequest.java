package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class ProjectRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectUrl;
    private String githubUrl;
}
