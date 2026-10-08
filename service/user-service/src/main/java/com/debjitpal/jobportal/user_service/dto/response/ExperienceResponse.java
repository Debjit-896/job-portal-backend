package com.debjitpal.jobportal.user_service.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ExperienceResponse {
    private UUID id;
    private String company;
    private String jobTitle;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
    private String description;
    private String location;
}
