package com.debjitpal.jobportal.dto.request;

import com.debjitpal.jobportal.domain.CompanySize;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import com.debjitpal.jobportal.dto.response.SocialLinksResponse;
import com.debjitpal.jobportal.validation.ValidFoundedYear;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String tagLine;
    private String description;
    private String logoUrl;
    private String coverImageUrl;

    @Pattern(regexp = "^(https?://).*", message = "Website URL should be valid")
    private String websiteUrl;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber;

    @Min(value = 1800, message = "Founded year seems too old!")
    @ValidFoundedYear
    private Integer foundedYear;

    @NotNull(message = "Company size is required")
    private CompanySize companySize;

    @NotNull(message = "Company type is required")
    private CompanyType companyType;

    @NotNull(message = "Industry type is required")
    private IndustryType industryType;

    private String registrationNumber;

    private List<SocialLinksResponse> socialLinks;
}
