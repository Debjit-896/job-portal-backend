package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.user_service.dto.response.LanguageResponse;
import com.debjitpal.jobportal.user_service.service.ProfileExtendedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
public class LanguageMasterController {

    private final ProfileExtendedService profileService;

    @GetMapping
    public ResponseEntity<List<LanguageResponse>> getAllLanguages() {
        return ResponseEntity.ok(profileService.getAllMasterLanguages());
    }
}
