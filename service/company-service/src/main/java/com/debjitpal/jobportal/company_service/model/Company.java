package com.debjitpal.jobportal.company_service.model;

import com.debjitpal.jobportal.domain.CompanySize;
import com.debjitpal.jobportal.domain.CompanyStatus;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    private String tagLine;
    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String websiteUrl;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private Integer foundedYear;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Enumerated(EnumType.STRING)
    private IndustryType industryType;

    @Enumerated(EnumType.STRING)
    private CompanyStatus companyStatus;

    @Column(unique = true)
    private String registrationNumber;

    @Column(unique = true, nullable = false)
    private Long ownerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<SocialLinks> socialLinks = new ArrayList<>();

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean isVerified = false;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime verifiedAt;
}
