package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.entity.EmailVerificationToken;
import com.debjitpal.jobportal.user_service.entity.PasswordResetToken;
import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.repository.EmailVerificationTokenRepository;
import com.debjitpal.jobportal.user_service.repository.PasswordResetTokenRepository;
import com.debjitpal.jobportal.user_service.repository.RefreshTokenRepository;
import com.debjitpal.jobportal.user_service.service.EmailService;
import com.debjitpal.jobportal.user_service.service.TokenService;
import com.debjitpal.jobportal.user_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

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

    private String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    private String hashOtp(String email, String rawOtp) {
        String normalized = (email != null ? email.trim().toLowerCase() : "") + ":" + (rawOtp != null ? rawOtp.trim() : "");
        return hashToken(normalized);
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
    public String generatePasswordResetOtp(User user) {
        java.util.List<PasswordResetToken> existing = passwordResetTokenRepository.findByUser(user);
        for (PasswordResetToken t : existing) {
            t.setUsed(true);
        }
        passwordResetTokenRepository.saveAll(existing);

        String rawOtp = generateOtp();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hashOtp(user.getEmail(), rawOtp))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        passwordResetTokenRepository.save(token);
        return rawOtp;
    }

    @Override
    public PasswordResetToken validatePasswordResetToken(String rawToken) {
        String hash = hashToken(rawToken);
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token is expired or already used");
        }
        return token;
    }

    @Override
    public PasswordResetToken validatePasswordResetOtp(String email, String rawOtp) {
        String hash = hashOtp(email, rawOtp);
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset OTP is expired or already used");
        }
        return token;
    }

    @Override
    public String verifyPasswordResetOtp(String email, String rawOtp) {
        PasswordResetToken token = validatePasswordResetOtp(email, rawOtp);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        User user = token.getUser();
        String resetToken = generateSecureToken();
        PasswordResetToken nextToken = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hashToken(resetToken))
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        passwordResetTokenRepository.save(nextToken);
        return resetToken;
    }

    @Override
    public void consumePasswordResetToken(String rawToken) {
        PasswordResetToken token = validatePasswordResetToken(rawToken);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    @Override
    public void consumePasswordResetOtp(String email, String rawOtp) {
        PasswordResetToken token = validatePasswordResetOtp(email, rawOtp);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    @Override
    public String generateEmailVerificationToken(User user) {
        return generateEmailVerificationOtp(user);
    }

    @Override
    @Transactional
    public String generateEmailVerificationOtp(User user) {
        // Invalidate all previous tokens for this user before issuing a fresh one
        emailVerificationTokenRepository.deleteAllByUser(user);
        emailVerificationTokenRepository.flush();

        String rawOtp = generateOtp();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .tokenHash(hashOtp(user.getEmail(), rawOtp))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        emailVerificationTokenRepository.save(token);

        // Send OTP to user's email — NOT to terminal
        emailService.sendOtpEmail(
                user.getEmail(),
                rawOtp,
                "Your AI Job Portal Email Verification OTP"
        );
        log.info("Email verification OTP sent to {}", user.getEmail());
        return rawOtp;
    }

    @Override
    public EmailVerificationToken validateEmailVerificationToken(String rawToken) {
        String hash = hashToken(rawToken);
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHashWithUser(hash)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));
        if (token.isUsed() || token.isExpired()) {
            throw new BadRequestException("Verification OTP is expired or already used");
        }
        return token;
    }

    @Override
    public EmailVerificationToken validateEmailVerificationOtp(String email, String rawOtp) {
        String hash = hashOtp(email, rawOtp);
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHashWithUser(hash)
                .orElseThrow(() -> new BadRequestException("Invalid verification code"));
        if (token.isUsed() || token.isExpired()) {
            throw new BadRequestException("Verification OTP is expired or already used");
        }
        return token;
    }

    @Override
    @Transactional
    public void consumeEmailVerificationToken(String rawToken) {
        EmailVerificationToken token = validateEmailVerificationToken(rawToken);
        emailVerificationTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void consumeEmailVerificationOtp(String email, String rawOtp) {
        EmailVerificationToken token = validateEmailVerificationOtp(email, rawOtp);
        // Delete instead of marking used — removes it from the DB permanently after use
        emailVerificationTokenRepository.delete(token);
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
