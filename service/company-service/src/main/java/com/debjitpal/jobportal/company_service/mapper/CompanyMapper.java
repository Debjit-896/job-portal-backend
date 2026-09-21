package com.debjitpal.jobportal.company_service.mapper;

import java.util.List;

import com.debjitpal.jobportal.company_service.model.Company;
import com.debjitpal.jobportal.company_service.model.SocialLinks;
import com.debjitpal.jobportal.dto.response.CompanyResponse;
import com.debjitpal.jobportal.dto.response.SocialLinksResponse;
import java.util.Collections;

public class CompanyMapper {

    public static SocialLinksResponse toSocialLinksResponse(SocialLinks socialLinks){
        return SocialLinksResponse.builder()
                .socialPlatformName(socialLinks.getSocialPlatformName())
                .socialPlatformUrl(socialLinks.getSocialPlatformUrl())
                .build();
    }

    public static CompanyResponse toResponse(Company company){
        List<SocialLinksResponse> socialLinks = company.getSocialLinks() == null? Collections.emptyList()
                : company.getSocialLinks().stream().map(CompanyMapper::toSocialLinksResponse).toList();

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .slug(company.getSlug())
                .tagLine(company.getTagLine())
                .websiteUrl(company.getWebsiteUrl())
                .logoUrl(company.getLogoUrl())
                .coverImageUrl(company.getCoverImageUrl())
                .email(company.getEmail())
                .phoneNumber(company.getPhoneNumber())
                .foundedYear(company.getFoundedYear())
                .companySize(company.getCompanySize())
                .companyType(company.getCompanyType())
                .industryType(company.getIndustryType())
                .companyStatus(company.getCompanyStatus())
                .registrationNumber(company.getRegistrationNumber())
                .active(company.getActive())
                .ownerId(company.getOwnerId())
                .socialLinks(socialLinks)
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .verifiedAt(company.getVerifiedAt())
                .build();
    }
}
