package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface ProfileExtendedService {

    // --- Certifications ---
    CertificationResponse createCertification(String email, CertificationRequest request);
    List<CertificationResponse> getCertifications(String email);
    CertificationResponse updateCertification(String email, UUID certificationId, CertificationRequest request);
    void deleteCertification(String email, UUID certificationId);

    // --- Projects ---
    ProjectResponse createProject(String email, ProjectRequest request);
    List<ProjectResponse> getProjects(String email);
    ProjectResponse updateProject(String email, UUID projectId, ProjectRequest request);
    void deleteProject(String email, UUID projectId);

    // --- Languages (Master) ---
    List<LanguageResponse> getAllMasterLanguages();

    // --- User Languages ---
    List<UserLanguageResponse> addUserLanguages(String email, List<UserLanguageRequest> requests);
    List<UserLanguageResponse> getUserLanguages(String email);
    List<UserLanguageResponse> replaceUserLanguages(String email, List<UserLanguageRequest> requests);
    void removeUserLanguage(String email, UUID languageId);

    // --- User Locations ---
    UserLocationResponse createLocation(String email, UserLocationRequest request);
    List<UserLocationResponse> getLocations(String email);
    UserLocationResponse updateLocation(String email, UUID locationId, UserLocationRequest request);
    void deleteLocation(String email, UUID locationId);

    // --- User Preferences ---
    UserPreferenceResponse getPreferences(String email);
    UserPreferenceResponse replacePreferences(String email, UserPreferenceRequest request);
    UserPreferenceResponse partiallyUpdatePreferences(String email, UserPreferenceRequest request);
}
