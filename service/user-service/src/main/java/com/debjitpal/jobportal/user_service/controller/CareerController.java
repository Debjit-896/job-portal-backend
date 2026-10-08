package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.user_service.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    // --- Education APIs ---
    
    @PostMapping("/educations")
    public ResponseEntity<EducationResponse> createEducation(Principal principal, @RequestBody EducationRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.createEducation(principal.getName(), request));
    }

    @GetMapping("/educations")
    public ResponseEntity<List<EducationResponse>> getEducations(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.getEducations(principal.getName()));
    }

    @GetMapping("/educations/{educationId}")
    public ResponseEntity<EducationResponse> getEducation(Principal principal, @PathVariable("educationId") UUID educationId) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.getEducation(principal.getName(), educationId));
    }

    @PutMapping("/educations/{educationId}")
    public ResponseEntity<EducationResponse> updateEducation(Principal principal, @PathVariable("educationId") UUID educationId, @RequestBody EducationRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.updateEducation(principal.getName(), educationId, request));
    }

    @DeleteMapping("/educations/{educationId}")
    public ResponseEntity<Void> deleteEducation(Principal principal, @PathVariable("educationId") UUID educationId) {
        if (principal == null) return ResponseEntity.status(401).build();
        careerService.deleteEducation(principal.getName(), educationId);
        return ResponseEntity.ok().build();
    }

    // --- Experience APIs ---

    @PostMapping("/experiences")
    public ResponseEntity<ExperienceResponse> createExperience(Principal principal, @RequestBody ExperienceRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.createExperience(principal.getName(), request));
    }

    @GetMapping("/experiences")
    public ResponseEntity<List<ExperienceResponse>> getExperiences(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.getExperiences(principal.getName()));
    }

    @GetMapping("/experiences/{experienceId}")
    public ResponseEntity<ExperienceResponse> getExperience(Principal principal, @PathVariable("experienceId") UUID experienceId) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.getExperience(principal.getName(), experienceId));
    }

    @PutMapping("/experiences/{experienceId}")
    public ResponseEntity<ExperienceResponse> updateExperience(Principal principal, @PathVariable("experienceId") UUID experienceId, @RequestBody ExperienceRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.updateExperience(principal.getName(), experienceId, request));
    }

    @DeleteMapping("/experiences/{experienceId}")
    public ResponseEntity<Void> deleteExperience(Principal principal, @PathVariable("experienceId") UUID experienceId) {
        if (principal == null) return ResponseEntity.status(401).build();
        careerService.deleteExperience(principal.getName(), experienceId);
        return ResponseEntity.ok().build();
    }

    // --- Skills APIs ---

    @PostMapping("/skills")
    public ResponseEntity<List<UserSkillResponse>> addUserSkills(Principal principal, @RequestBody List<UserSkillRequest> requests) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.addUserSkills(principal.getName(), requests));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.getUserSkills(principal.getName()));
    }

    @PutMapping("/skills")
    public ResponseEntity<List<UserSkillResponse>> replaceUserSkills(Principal principal, @RequestBody List<UserSkillRequest> requests) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(careerService.replaceUserSkills(principal.getName(), requests));
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Void> removeUserSkill(Principal principal, @PathVariable("skillId") UUID skillId) {
        if (principal == null) return ResponseEntity.status(401).build();
        careerService.removeUserSkill(principal.getName(), skillId);
        return ResponseEntity.ok().build();
    }
}
