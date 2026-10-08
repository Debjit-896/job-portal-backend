package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.user_service.service.ProfileExtendedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class ProfileExtendedController {

    private final ProfileExtendedService profileService;

    // --- Certifications APIs ---

    @PostMapping("/certifications")
    public ResponseEntity<CertificationResponse> createCertification(Principal principal, @RequestBody CertificationRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.createCertification(principal.getName(), request));
    }

    @GetMapping("/certifications")
    public ResponseEntity<List<CertificationResponse>> getCertifications(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.getCertifications(principal.getName()));
    }

    @PutMapping("/certifications/{certificationId}")
    public ResponseEntity<CertificationResponse> updateCertification(Principal principal, @PathVariable("certificationId") UUID certificationId, @RequestBody CertificationRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.updateCertification(principal.getName(), certificationId, request));
    }

    @DeleteMapping("/certifications/{certificationId}")
    public ResponseEntity<Void> deleteCertification(Principal principal, @PathVariable("certificationId") UUID certificationId) {
        if (principal == null) return ResponseEntity.status(401).build();
        profileService.deleteCertification(principal.getName(), certificationId);
        return ResponseEntity.ok().build();
    }

    // --- Projects APIs ---

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(Principal principal, @RequestBody ProjectRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.createProject(principal.getName(), request));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getProjects(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.getProjects(principal.getName()));
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(Principal principal, @PathVariable("projectId") UUID projectId, @RequestBody ProjectRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.updateProject(principal.getName(), projectId, request));
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(Principal principal, @PathVariable("projectId") UUID projectId) {
        if (principal == null) return ResponseEntity.status(401).build();
        profileService.deleteProject(principal.getName(), projectId);
        return ResponseEntity.ok().build();
    }

    // --- Languages APIs ---

    @PostMapping("/languages")
    public ResponseEntity<List<UserLanguageResponse>> addUserLanguages(Principal principal, @RequestBody List<UserLanguageRequest> requests) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.addUserLanguages(principal.getName(), requests));
    }

    @GetMapping("/languages")
    public ResponseEntity<List<UserLanguageResponse>> getUserLanguages(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.getUserLanguages(principal.getName()));
    }

    @PutMapping("/languages")
    public ResponseEntity<List<UserLanguageResponse>> replaceUserLanguages(Principal principal, @RequestBody List<UserLanguageRequest> requests) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.replaceUserLanguages(principal.getName(), requests));
    }

    @DeleteMapping("/languages/{languageId}")
    public ResponseEntity<Void> removeUserLanguage(Principal principal, @PathVariable("languageId") UUID languageId) {
        if (principal == null) return ResponseEntity.status(401).build();
        profileService.removeUserLanguage(principal.getName(), languageId);
        return ResponseEntity.ok().build();
    }

    // --- Locations APIs ---

    @PostMapping("/locations")
    public ResponseEntity<UserLocationResponse> createLocation(Principal principal, @RequestBody UserLocationRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.createLocation(principal.getName(), request));
    }

    @GetMapping("/locations")
    public ResponseEntity<List<UserLocationResponse>> getLocations(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.getLocations(principal.getName()));
    }

    @PutMapping("/locations/{locationId}")
    public ResponseEntity<UserLocationResponse> updateLocation(Principal principal, @PathVariable("locationId") UUID locationId, @RequestBody UserLocationRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.updateLocation(principal.getName(), locationId, request));
    }

    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<Void> deleteLocation(Principal principal, @PathVariable("locationId") UUID locationId) {
        if (principal == null) return ResponseEntity.status(401).build();
        profileService.deleteLocation(principal.getName(), locationId);
        return ResponseEntity.ok().build();
    }

    // --- Preferences APIs ---

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferenceResponse> getPreferences(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.getPreferences(principal.getName()));
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferenceResponse> replacePreferences(Principal principal, @RequestBody UserPreferenceRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.replacePreferences(principal.getName(), request));
    }

    @PatchMapping("/preferences")
    public ResponseEntity<UserPreferenceResponse> partiallyUpdatePreferences(Principal principal, @RequestBody UserPreferenceRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileService.partiallyUpdatePreferences(principal.getName(), request));
    }
}
