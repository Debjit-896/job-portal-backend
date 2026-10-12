package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.user_service.entity.PasswordResetToken;
import com.debjitpal.jobportal.user_service.entity.EmailVerificationToken;

public interface TokenService {
    String generateRefreshToken(User user, String deviceInfo, String ipAddress, String userAgent);
    RefreshToken validateRefreshToken(String rawToken);
    void revokeRefreshToken(String rawToken);

    String generatePasswordResetToken(User user);
    String generatePasswordResetOtp(User user);
    PasswordResetToken validatePasswordResetToken(String rawToken);
    PasswordResetToken validatePasswordResetOtp(String email, String rawOtp);
    String verifyPasswordResetOtp(String email, String rawOtp);
    void consumePasswordResetToken(String rawToken);
    void consumePasswordResetOtp(String email, String rawOtp);

    String generateEmailVerificationToken(User user);
    String generateEmailVerificationOtp(User user);
    EmailVerificationToken validateEmailVerificationToken(String rawToken);
    EmailVerificationToken validateEmailVerificationOtp(String email, String rawOtp);
    void consumeEmailVerificationToken(String rawToken);
    void consumeEmailVerificationOtp(String email, String rawOtp);

    void revokeAllRefreshTokensForUser(User user);
}
