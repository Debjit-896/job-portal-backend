package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.entity.EmailVerificationToken;
import com.debjitpal.jobportal.user_service.entity.PasswordResetToken;
import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.repository.EmailVerificationTokenRepository;
import com.debjitpal.jobportal.user_service.repository.PasswordResetTokenRepository;
import com.debjitpal.jobportal.user_service.repository.RefreshTokenRepository;
import com.debjitpal.jobportal.user_service.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    
    private final SecureRandom secureRandom = new SecureRandom();

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    @Override
    public String generateRefreshToken(User user, String deviceInfo, String ipAddress, String userAgent) {
        String rawToken = generateSecureToken();
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        refreshTokenRepository.save(token);
        return rawToken;
    }

    @Override
    public RefreshToken validateRefreshToken(String rawToken) {
        String hash = hashToken(rawToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }
        return token;
    }

    @Override
    public void revokeRefreshToken(String rawToken) {
        String hash = hashToken(rawToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Override
    public String generatePasswordResetToken(User user) {
        String rawToken = generateSecureToken();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        passwordResetTokenRepository.save(token);
        return rawToken;
    }

    @Override
    public PasswordResetToken validatePasswordResetToken(String rawToken) {
        String hash = hashToken(rawToken);
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token is expired or already used");
        }
        return token;
    }

    @Override
    public void consumePasswordResetToken(String rawToken) {
        PasswordResetToken token = validatePasswordResetToken(rawToken);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    @Override
    public String generateEmailVerificationToken(User user) {
        String rawToken = generateSecureToken();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(1))
                .used(false)
                .build();
        emailVerificationTokenRepository.save(token);
        return rawToken;
    }

    @Override
    public EmailVerificationToken validateEmailVerificationToken(String rawToken) {
        String hash = hashToken(rawToken);
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token is expired or already used");
        }
        return token;
    }

    @Override
    public void consumeEmailVerificationToken(String rawToken) {
        EmailVerificationToken token = validateEmailVerificationToken(rawToken);
        token.setUsed(true);
        emailVerificationTokenRepository.save(token);
    }

    @Override
    public void revokeAllRefreshTokensForUser(User user) {
        java.util.List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }
        refreshTokenRepository.saveAll(tokens);
    }
}
