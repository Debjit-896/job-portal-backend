package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
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

    @PostMapping("/oauth-login")
    public ResponseEntity<AuthResponse> oauthLogin(
            @RequestBody @Valid OAuthLoginRequest authRequest
    ) {
        return ResponseEntity.ok(authService.oauthLogin(authRequest));
    }

    @PostMapping("/link-oauth")
    public ResponseEntity<AuthResponse> linkOAuthAccount(
            @RequestBody @Valid LinkOAuthRequest linkRequest,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.linkOAuthAccount(linkRequest, principal.getName()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody @Valid LogoutRequest request
    ) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        authService.logoutAll(principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        authService.changePassword(request, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.me(principal.getName()));
    }

    @PostMapping("/email-verification/send")
    public ResponseEntity<Void> sendEmailVerification(
            @RequestBody @Valid EmailVerificationRequest request
    ) {
        authService.sendEmailVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-verification/confirm")
    public ResponseEntity<Void> confirmEmail(
            @RequestBody @Valid ConfirmEmailRequest request
    ) {
        authService.confirmEmail(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/oauth/accounts")
    public ResponseEntity<java.util.List<LinkedOAuthAccountResponse>> getLinkedOAuthAccounts(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.getLinkedOAuthAccounts(principal.getName()));
    }

    @DeleteMapping("/oauth/accounts/{provider}")
    public ResponseEntity<Void> unlinkOAuthAccount(
            @PathVariable String provider,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        authService.unlinkOAuthAccount(provider, principal.getName());
        return ResponseEntity.ok().build();
    }
}
