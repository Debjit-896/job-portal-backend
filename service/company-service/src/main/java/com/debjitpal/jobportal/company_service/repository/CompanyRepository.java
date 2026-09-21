package com.debjitpal.jobportal.company_service.repository;

import com.debjitpal.jobportal.company_service.model.Company;
import com.debjitpal.jobportal.domain.CompanyStatus;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByOwnerId(Long ownerId);
    boolean existsByOwnerId(Long ownerId);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT c FROM Company c WHERE " +
            "(:companyType IS NULL OR c.companyType = :companyType) AND " +
            "(:industryType IS NULL OR c.industryType = :industryType) AND " +
            "(:companyStatus IS NULL OR c.companyStatus = :companyStatus)")
    List<Company> findByFilters(@Param("companyType") CompanyType companyType,
                                @Param("industryType") IndustryType industryType,
                                @Param("companyStatus") CompanyStatus companyStatus);
}
