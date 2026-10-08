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
    PasswordResetToken validatePasswordResetToken(String rawToken);
    void consumePasswordResetToken(String rawToken);

    String generateEmailVerificationToken(User user);
    EmailVerificationToken validateEmailVerificationToken(String rawToken);
    void consumeEmailVerificationToken(String rawToken);

    void revokeAllRefreshTokensForUser(User user);
}
