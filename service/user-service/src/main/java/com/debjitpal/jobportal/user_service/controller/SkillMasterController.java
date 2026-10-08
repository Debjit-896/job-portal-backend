package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.user_service.dto.response.SkillResponse;
import com.debjitpal.jobportal.user_service.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillMasterController {

    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(careerService.getAllMasterSkills());
    }
}
