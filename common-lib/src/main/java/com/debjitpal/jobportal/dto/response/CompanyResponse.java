package com.debjitpal.jobportal.dto.response;

import com.debjitpal.jobportal.domain.CompanySize;
import com.debjitpal.jobportal.domain.CompanyStatus;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {

    private UUID id;
    private String name;
    private String slug;
    private String tagLine;
    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String websiteUrl;
    private Integer foundedYear;
    private String email;
    private String phoneNumber;
    private CompanySize companySize;
    private CompanyType companyType;
    private IndustryType industryType;
    private CompanyStatus companyStatus;
    private String registrationNumber;
    private Long ownerId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime verifiedAt;

    private List<SocialLinksResponse> socialLinks;
}
