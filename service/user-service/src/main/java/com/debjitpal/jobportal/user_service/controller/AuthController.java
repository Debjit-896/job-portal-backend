package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.user_service.dto.AuthResponse;
import com.debjitpal.jobportal.user_service.dto.LoginRequest;
import com.debjitpal.jobportal.user_service.dto.SignupRequest;
import com.debjitpal.jobportal.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @RequestBody @Valid SignupRequest authRequest
    ) {
        return ResponseEntity.ok(authService.signup(authRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest authRequest
    ) {
        return ResponseEntity.ok(authService.login(authRequest));
    }
}
