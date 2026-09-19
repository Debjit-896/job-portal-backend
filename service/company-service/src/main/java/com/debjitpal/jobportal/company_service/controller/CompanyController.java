package com.debjitpal.jobportal.company_service.controller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.debjitpal.jobportal.company_service.service.CompanyService;
import com.debjitpal.jobportal.domain.CompanyStatus;
import com.debjitpal.jobportal.domain.CompanyType;
import com.debjitpal.jobportal.domain.IndustryType;
import com.debjitpal.jobportal.dto.request.CompanyRequest;
import com.debjitpal.jobportal.dto.response.CompanyResponse;
import com.debjitpal.jobportal.dto.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
        @RequestHeader("X-User-Id") Long ownerId,
        @RequestBody @Valid  CompanyRequest request) throws Exception{
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(ownerId,request));
    }

    @GetMapping("/my-company")
    public ResponseEntity<CompanyResponse> getMyCompany(@RequestHeader("X-User-Id") Long ownerId) throws Exception{
        return ResponseEntity.status(HttpStatus.OK)
                .body(companyService.getMyCompany(ownerId));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateMyCompany(
        @RequestHeader("X-User-Id") Long ownerId,
        @PathVariable UUID companyId,
        @RequestBody @Valid CompanyRequest request) throws Exception{
        return ResponseEntity.status(HttpStatus.OK)
                .body(companyService.updateCompany(ownerId, companyId, request));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable UUID companyId) throws Exception{
        return ResponseEntity.status(HttpStatus.OK)
                .body(companyService.getCompanyById(companyId));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(
            @RequestParam(required = false) CompanyType companyType,
            @RequestParam(required = false) IndustryType industryType,
            @RequestParam(required = false) CompanyStatus companyStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(companyService.getAllCompanies(companyType, industryType, companyStatus));
    }

    @PatchMapping("/{companyId}/verify")
    public ResponseEntity<CompanyResponse> verifyCompany(@PathVariable UUID companyId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK)
                .body(companyService.verifyCompany(companyId));
    }

    @PatchMapping("/{companyId}/deactivate")
    public ResponseEntity<CompanyResponse> deactivateCompany(@PathVariable UUID companyId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK)
                .body(companyService.deActivateCompany(companyId));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse> deleteCompany(@PathVariable UUID companyId,
            @RequestHeader("X-User-Id") Long ownerId) throws Exception {
        companyService.deleteCompany(companyId, ownerId);
        return ResponseEntity.ok(new ApiResponse("Company deleted successfully",true));
    }
}
