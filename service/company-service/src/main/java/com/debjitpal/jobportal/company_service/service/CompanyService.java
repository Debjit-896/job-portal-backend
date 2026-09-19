package com.debjitpal.jobportal.company_service.service;

import com.debjitpal.jobportal.company_service.model.Company;
import com.debjitpal.jobportal.domain.CompanyStatus;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import com.debjitpal.jobportal.dto.request.CompanyRequest;
import com.debjitpal.jobportal.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyResponse createCompany(Long ownerId, CompanyRequest request);
    CompanyResponse getCompanyById(UUID companyId);
    CompanyResponse getMyCompany(Long ownerId);
    List<CompanyResponse> getAllCompanies(CompanyType companyType,
                                          IndustryType industryType,
                                          CompanyStatus companyStatus);

    CompanyResponse updateCompany(Long ownerId, UUID companyId, CompanyRequest request);
    CompanyResponse verifyCompany(UUID companyId) throws Exception;
    void deleteCompany(UUID companyId, Long ownerId) throws Exception;
    CompanyResponse deActivateCompany(UUID companyId) throws Exception;

    // Only for inter service call
    Company getCompanyEntityById(UUID companyId);

}
