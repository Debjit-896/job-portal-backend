package com.debjitpal.jobportal.user_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProjectResponse {
    private UUID id;
    private String name;
    private String description;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectUrl;
    private String githubUrl;
}
