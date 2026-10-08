package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.dto.response.UserResponse;

public interface AuthService {

    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse oauthLogin(OAuthLoginRequest request);
    AuthResponse linkOAuthAccount(LinkOAuthRequest request, String userEmail);

    AuthResponse refresh(RefreshTokenRequest request);
    void logout(LogoutRequest request);
    void logoutAll(String userEmail);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void changePassword(ChangePasswordRequest request, String userEmail);
    UserResponse me(String userEmail);
    
    void sendEmailVerification(EmailVerificationRequest request);
    void confirmEmail(ConfirmEmailRequest request);

    java.util.List<LinkedOAuthAccountResponse> getLinkedOAuthAccounts(String userEmail);
    void unlinkOAuthAccount(String provider, String userEmail);
}
