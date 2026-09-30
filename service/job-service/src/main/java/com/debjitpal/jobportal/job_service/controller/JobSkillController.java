package com.debjitpal.jobportal.job_service.controller;

import com.debjitpal.jobportal.dto.response.ApiResponse;
import com.debjitpal.jobportal.dto.response.JobSkillResponse;
import com.debjitpal.jobportal.job_service.payload.JobSkillRequest;
import com.debjitpal.jobportal.job_service.service.JobSkillService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/job-skills")
@RequiredArgsConstructor
public class JobSkillController {
    private final JobSkillService skillService;

    @PostMapping
    public ResponseEntity<JobSkillResponse> createSkill(@RequestBody @Valid JobSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(request));
    }
        
    @GetMapping("/{id}")
    public ResponseEntity<JobSkillResponse> getSkillById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(skillService.getSkillById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobSkillResponse>> getAllSkills() {
        return ResponseEntity.status(HttpStatus.OK).body(skillService.getAllSkills());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobSkillResponse> updateSkill(@PathVariable Long id,
        @RequestBody @Valid JobSkillRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(skillService.updateSkill(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(new ApiResponse("Skill deleted successfully", true));
    }
}
