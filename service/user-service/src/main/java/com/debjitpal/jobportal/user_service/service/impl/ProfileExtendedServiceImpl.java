package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.user_service.entity.*;
import com.debjitpal.jobportal.user_service.repository.*;
import com.debjitpal.jobportal.user_service.service.ProfileExtendedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileExtendedServiceImpl implements ProfileExtendedService {

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;
    private final ProjectRepository projectRepository;
    private final LanguageRepository languageRepository;
    private final UserLanguageRepository userLanguageRepository;
    private final UserLocationRepository userLocationRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // --- Certifications ---

    private CertificationResponse mapCertification(Certification cert) {
        return CertificationResponse.builder()
                .id(cert.getId())
                .name(cert.getName())
                .issuingOrganization(cert.getIssuingOrganization())
                .issueDate(cert.getIssueDate())
                .expiryDate(cert.getExpiryDate())
                .credentialId(cert.getCredentialId())
                .credentialUrl(cert.getCredentialUrl())
                .description(cert.getDescription())
                .build();
    }

    @Override
    @Transactional
    public CertificationResponse createCertification(String email, CertificationRequest request) {
        User user = getUserByEmail(email);
        Certification cert = Certification.builder()
                .user(user)
                .name(request.getName())
                .issuingOrganization(request.getIssuingOrganization())
                .issueDate(request.getIssueDate())
                .expiryDate(request.getExpiryDate())
                .credentialId(request.getCredentialId())
                .credentialUrl(request.getCredentialUrl())
                .description(request.getDescription())
                .build();
        return mapCertification(certificationRepository.save(cert));
    }

    @Override
    public List<CertificationResponse> getCertifications(String email) {
        User user = getUserByEmail(email);
        return certificationRepository.findByUserId(user.getId()).stream()
                .map(this::mapCertification).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CertificationResponse updateCertification(String email, UUID certificationId, CertificationRequest request) {
        User user = getUserByEmail(email);
        Certification cert = certificationRepository.findById(certificationId)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        
        cert.setName(request.getName());
        cert.setIssuingOrganization(request.getIssuingOrganization());
        cert.setIssueDate(request.getIssueDate());
        cert.setExpiryDate(request.getExpiryDate());
        cert.setCredentialId(request.getCredentialId());
        cert.setCredentialUrl(request.getCredentialUrl());
        cert.setDescription(request.getDescription());
        
        return mapCertification(certificationRepository.save(cert));
    }

    @Override
    @Transactional
    public void deleteCertification(String email, UUID certificationId) {
        User user = getUserByEmail(email);
        Certification cert = certificationRepository.findById(certificationId)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        certificationRepository.delete(cert);
    }

    // --- Projects ---

    private ProjectResponse mapProject(Project proj) {
        return ProjectResponse.builder()
                .id(proj.getId())
                .name(proj.getName())
                .description(proj.getDescription())
                .role(proj.getRole())
                .startDate(proj.getStartDate())
                .endDate(proj.getEndDate())
                .projectUrl(proj.getProjectUrl())
                .githubUrl(proj.getGithubUrl())
                .build();
    }

    @Override
    @Transactional
    public ProjectResponse createProject(String email, ProjectRequest request) {
        User user = getUserByEmail(email);
        Project proj = Project.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .role(request.getRole())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .projectUrl(request.getProjectUrl())
                .githubUrl(request.getGithubUrl())
                .build();
        return mapProject(projectRepository.save(proj));
    }

    @Override
    public List<ProjectResponse> getProjects(String email) {
        User user = getUserByEmail(email);
        return projectRepository.findByUserId(user.getId()).stream()
                .map(this::mapProject).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(String email, UUID projectId, ProjectRequest request) {
        User user = getUserByEmail(email);
        Project proj = projectRepository.findById(projectId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Project not found"));

        proj.setName(request.getName());
        proj.setDescription(request.getDescription());
        proj.setRole(request.getRole());
        proj.setStartDate(request.getStartDate());
        proj.setEndDate(request.getEndDate());
        proj.setProjectUrl(request.getProjectUrl());
        proj.setGithubUrl(request.getGithubUrl());

        return mapProject(projectRepository.save(proj));
    }

    @Override
    @Transactional
    public void deleteProject(String email, UUID projectId) {
        User user = getUserByEmail(email);
        Project proj = projectRepository.findById(projectId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(proj);
    }

    // --- Languages ---

    private LanguageResponse mapLanguage(Language lang) {
        return LanguageResponse.builder()
                .id(lang.getId())
                .name(lang.getName())
                .build();
    }

    private UserLanguageResponse mapUserLanguage(UserLanguage ul) {
        return UserLanguageResponse.builder()
                .language(mapLanguage(ul.getLanguage()))
                .proficiency(ul.getProficiency())
                .build();
    }

    @Override
    public List<LanguageResponse> getAllMasterLanguages() {
        return languageRepository.findAll().stream()
                .map(this::mapLanguage).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserLanguageResponse> addUserLanguages(String email, List<UserLanguageRequest> requests) {
        User user = getUserByEmail(email);
        for (UserLanguageRequest req : requests) {
            Language lang = languageRepository.findById(req.getLanguageId())
                    .orElseThrow(() -> new RuntimeException("Language not found in master list: " + req.getLanguageId()));
            
            UserLanguageId id = new UserLanguageId(user.getId(), lang.getId());
            if (!userLanguageRepository.existsById(id)) {
                UserLanguage userLanguage = UserLanguage.builder()
                        .id(id)
                        .user(user)
                        .language(lang)
                        .proficiency(req.getProficiency())
                        .build();
                userLanguageRepository.save(userLanguage);
            }
        }
        return getUserLanguages(email);
    }

    @Override
    public List<UserLanguageResponse> getUserLanguages(String email) {
        User user = getUserByEmail(email);
        return userLanguageRepository.findByIdUserId(user.getId()).stream()
                .map(this::mapUserLanguage).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserLanguageResponse> replaceUserLanguages(String email, List<UserLanguageRequest> requests) {
        User user = getUserByEmail(email);
        userLanguageRepository.deleteByIdUserId(user.getId());
        userLanguageRepository.flush();
        return addUserLanguages(email, requests);
    }

    @Override
    @Transactional
    public void removeUserLanguage(String email, UUID languageId) {
        User user = getUserByEmail(email);
        UserLanguageId id = new UserLanguageId(user.getId(), languageId);
        userLanguageRepository.findById(id).ifPresent(userLanguageRepository::delete);
    }

    // --- User Locations ---

    private UserLocationResponse mapUserLocation(UserLocation loc) {
        return UserLocationResponse.builder()
                .id(loc.getId())
                .addressLine1(loc.getAddressLine1())
                .addressLine2(loc.getAddressLine2())
                .city(loc.getCity())
                .state(loc.getState())
                .country(loc.getCountry())
                .postalCode(loc.getPostalCode())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .build();
    }

    @Override
    @Transactional
    public UserLocationResponse createLocation(String email, UserLocationRequest request) {
        User user = getUserByEmail(email);
        // Assuming 1 to many location for now, though entity says @OneToOne, wait let's check
        // If entity says @OneToOne, we should check if one exists.
        List<UserLocation> existing = userLocationRepository.findByUserId(user.getId());
        if (!existing.isEmpty()) {
            throw new RuntimeException("Location already exists. Use PUT to update.");
        }
        
        UserLocation loc = UserLocation.builder()
                .user(user)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        return mapUserLocation(userLocationRepository.save(loc));
    }

    @Override
    public List<UserLocationResponse> getLocations(String email) {
        User user = getUserByEmail(email);
        return userLocationRepository.findByUserId(user.getId()).stream()
                .map(this::mapUserLocation).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserLocationResponse updateLocation(String email, UUID locationId, UserLocationRequest request) {
        User user = getUserByEmail(email);
        UserLocation loc = userLocationRepository.findById(locationId)
                .filter(l -> l.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Location not found"));

        loc.setAddressLine1(request.getAddressLine1());
        loc.setAddressLine2(request.getAddressLine2());
        loc.setCity(request.getCity());
        loc.setState(request.getState());
        loc.setCountry(request.getCountry());
        loc.setPostalCode(request.getPostalCode());
        loc.setLatitude(request.getLatitude());
        loc.setLongitude(request.getLongitude());

        return mapUserLocation(userLocationRepository.save(loc));
    }

    @Override
    @Transactional
    public void deleteLocation(String email, UUID locationId) {
        User user = getUserByEmail(email);
        UserLocation loc = userLocationRepository.findById(locationId)
                .filter(l -> l.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Location not found"));
        userLocationRepository.delete(loc);
    }

    // --- User Preferences ---

    private UserPreferenceResponse mapUserPreference(UserPreference pref) {
        return UserPreferenceResponse.builder()
                .id(pref.getId())
                .preferredJobTitles(pref.getPreferredJobTitles())
                .preferredSkills(pref.getPreferredSkills())
                .expectedMinSalary(pref.getExpectedMinSalary())
                .expectedMaxSalary(pref.getExpectedMaxSalary())
                .preferredLocation(pref.getPreferredLocation())
                .preferredJobType(pref.getPreferredJobType())
                .preferredWorkMode(pref.getPreferredWorkMode())
                .experienceLevel(pref.getExperienceLevel())
                .willingToRelocate(pref.getWillingToRelocate())
                .noticePeriodDays(pref.getNoticePeriodDays())
                .build();
    }

    @Override
    public UserPreferenceResponse getPreferences(String email) {
        User user = getUserByEmail(email);
        UserPreference pref = userPreferenceRepository.findByUserId(user.getId())
                .orElse(UserPreference.builder().user(user).build());
        return mapUserPreference(pref);
    }

    @Override
    @Transactional
    public UserPreferenceResponse replacePreferences(String email, UserPreferenceRequest request) {
        User user = getUserByEmail(email);
        UserPreference pref = userPreferenceRepository.findByUserId(user.getId())
                .orElse(UserPreference.builder().user(user).build());
        
        pref.setPreferredJobTitles(request.getPreferredJobTitles());
        pref.setPreferredSkills(request.getPreferredSkills());
        pref.setExpectedMinSalary(request.getExpectedMinSalary());
        pref.setExpectedMaxSalary(request.getExpectedMaxSalary());
        pref.setPreferredLocation(request.getPreferredLocation());
        pref.setPreferredJobType(request.getPreferredJobType());
        pref.setPreferredWorkMode(request.getPreferredWorkMode());
        pref.setExperienceLevel(request.getExperienceLevel());
        pref.setWillingToRelocate(request.getWillingToRelocate());
        pref.setNoticePeriodDays(request.getNoticePeriodDays());
        
        return mapUserPreference(userPreferenceRepository.save(pref));
    }

    @Override
    @Transactional
    public UserPreferenceResponse partiallyUpdatePreferences(String email, UserPreferenceRequest request) {
        User user = getUserByEmail(email);
        UserPreference pref = userPreferenceRepository.findByUserId(user.getId())
                .orElse(UserPreference.builder().user(user).build());
                
        if (request.getPreferredJobTitles() != null) pref.setPreferredJobTitles(request.getPreferredJobTitles());
        if (request.getPreferredSkills() != null) pref.setPreferredSkills(request.getPreferredSkills());
        if (request.getExpectedMinSalary() != null) pref.setExpectedMinSalary(request.getExpectedMinSalary());
        if (request.getExpectedMaxSalary() != null) pref.setExpectedMaxSalary(request.getExpectedMaxSalary());
        if (request.getPreferredLocation() != null) pref.setPreferredLocation(request.getPreferredLocation());
        if (request.getPreferredJobType() != null) pref.setPreferredJobType(request.getPreferredJobType());
        if (request.getPreferredWorkMode() != null) pref.setPreferredWorkMode(request.getPreferredWorkMode());
        if (request.getExperienceLevel() != null) pref.setExperienceLevel(request.getExperienceLevel());
        if (request.getWillingToRelocate() != null) pref.setWillingToRelocate(request.getWillingToRelocate());
        if (request.getNoticePeriodDays() != null) pref.setNoticePeriodDays(request.getNoticePeriodDays());
        
        return mapUserPreference(userPreferenceRepository.save(pref));
    }
}
