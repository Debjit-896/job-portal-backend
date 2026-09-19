package com.debjitpal.jobportal.company_service.service.Impl;

import com.debjitpal.jobportal.company_service.model.Company;
import com.debjitpal.jobportal.company_service.model.SocialLinks;
import com.debjitpal.jobportal.company_service.repository.CompanyRepository;
import com.debjitpal.jobportal.company_service.mapper.CompanyMapper;
import com.debjitpal.jobportal.company_service.service.CompanyService;
import com.debjitpal.jobportal.domain.CompanyStatus;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import com.debjitpal.jobportal.dto.request.CompanyRequest;
import com.debjitpal.jobportal.dto.response.CompanyResponse;
import com.debjitpal.jobportal.dto.response.SocialLinksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    @Override
    public CompanyResponse createCompany(Long ownerId, CompanyRequest request) {

        if (companyRepository.existsByOwnerId(ownerId)) {
            throw new IllegalArgumentException("Company already exists for this owner");
        }

        if (companyRepository.existByName(request.getName())) {
            throw new IllegalArgumentException("Company name already exists");
        }

        if (request.getRegistrationNumber() != null && companyRepository.existByRegistrationNumber(request.getRegistrationNumber())) {
            throw new IllegalArgumentException("Company registration number already exists. Please provide a different registration number.");
        }

        String slug = generateUniqueSlug(request.getName());
        Company company = Company.builder()
                .name(request.getName())
                .slug(slug)
                .tagLine(request.getTagLine())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .coverImageUrl(request.getCoverImageUrl())
                .websiteUrl(request.getWebsiteUrl())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .registrationNumber(request.getRegistrationNumber())
                .foundedYear(request.getFoundedYear())
                .companySize(request.getCompanySize())
                .companyType(request.getCompanyType())
                .industryType(request.getIndustryType())
                .ownerId(ownerId)
                .socialLinks(mapSocialLinks(request.getSocialLinks()))
                .build();
        Company savedCompany = companyRepository.save(company);
        return CompanyMapper.toResponse(savedCompany);
    }

    private List<SocialLinks> mapSocialLinks(List<SocialLinksResponse> socialLinks) {
        if (socialLinks == null || socialLinks.isEmpty()){
            return new ArrayList<SocialLinks>();
        }
        return socialLinks.stream()
                .map(e->SocialLinks.builder()
                        .socialPlatformName(e.getSocialPlatformName())
                        .socialPlatformUrl(e.getSocialPlatformUrl())
                        .build())
                .collect(Collectors.toList());
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9\\s-]+", "")
                .trim().replaceAll("[\\s-]+", "-");

        if (!companyRepository.existBySlug(baseSlug)) {
            return baseSlug;
        }
        
        int counter = 1;

        while (companyRepository.existBySlug(baseSlug + "-" + counter)) {
            counter++;
        }

        return baseSlug + "-" + counter;
    }

    @Override
    public CompanyResponse getCompanyById(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found!"));
        return CompanyMapper.toResponse(company);
    }

    @Override
    public CompanyResponse getMyCompany(Long ownerId) {
        Company company = companyRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found!"));
        return CompanyMapper.toResponse(company);
    }

    @Override
    public List<CompanyResponse> getAllCompanies(CompanyType companyType, IndustryType industryType, CompanyStatus companyStatus) {
        return companyRepository.findByFilters(companyType, industryType, companyStatus)
                .stream()
                .map(CompanyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse updateCompany(Long ownerId, UUID companyId, CompanyRequest request) {
        Company company = getCompanyEntityById(companyId);
        
        if (!company.getName().equals(request.getName()) && companyRepository.existByName(request.getName())){
            throw new IllegalArgumentException("Company name already exists");
        }

        if (request.getRegistrationNumber() != null &&
        !request.getRegistrationNumber().equals(company.getRegistrationNumber()) &&
        companyRepository.existByRegistrationNumber(request.getRegistrationNumber())){
            throw new IllegalArgumentException("Registration number already exists");
        }

        company.setName(request.getName());
        company.setTagLine(request.getTagLine());
        company.setDescription(request.getDescription());
        company.setLogoUrl(request.getLogoUrl());
        company.setCoverImageUrl(request.getCoverImageUrl());
        company.setWebsiteUrl(request.getWebsiteUrl());
        company.setEmail(request.getEmail());
        company.setPhoneNumber(request.getPhoneNumber());
        company.setRegistrationNumber(request.getRegistrationNumber());
        company.setFoundedYear(request.getFoundedYear());
        company.setCompanySize(request.getCompanySize());
        company.setCompanyType(request.getCompanyType());
        company.setIndustryType(request.getIndustryType());
        company.setSocialLinks(mapSocialLinks(request.getSocialLinks()));
        
        return CompanyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    public CompanyResponse verifyCompany(UUID companyId) throws Exception {
        Company company = getCompanyEntityById(companyId);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setIsVerified(true);
        return CompanyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    public void deleteCompany(UUID companyId, Long ownerId){
        Company company = getCompanyEntityById(companyId);
        assertOwner(company, ownerId);
        companyRepository.delete(company);
    }

    private void assertOwner(Company company, Long ownerId) {
		if(!company.getOwnerId().equals(ownerId)){
            throw new IllegalArgumentException("You are not the owner of this company");
        }
	}

	@Override
    public CompanyResponse deActivateCompany(UUID companyId) throws Exception {
        Company company = getCompanyEntityById(companyId);
        company.setCompanyStatus(CompanyStatus.SUSPENDED);
        company.setIsVerified(false);
        return CompanyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    public Company getCompanyEntityById(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found!"));
    }
}
