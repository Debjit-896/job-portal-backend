package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExperienceRequest {
    private String company;
    private String jobTitle;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
    private String description;
    private String location;
}
