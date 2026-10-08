package com.debjitpal.jobportal.user_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CertificationRequest {
    private String name;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String credentialId;
    private String credentialUrl;
    private String description;
}