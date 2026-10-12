package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.exception.BadRequestException;
import com.debjitpal.jobportal.user_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Override
    public void sendOtpEmail(String toEmail, String otp, String subject) {
        if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
            log.error("SMTP Configuration missing: MAIL_USERNAME or MAIL_PASSWORD is not set. Cannot send email to {}.", toEmail);
            throw new BadRequestException("Email service is not configured. Please configure MAIL_USERNAME and a 16-character Google App Password (MAIL_PASSWORD).");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailUsername);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildOtpEmailHtml(otp), true);

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
        } catch (MailAuthenticationException e) {
            log.error("Failed to authenticate with smtp.gmail.com: {}. Ensure MAIL_USERNAME is your full Gmail address and MAIL_PASSWORD is a 16-character Google App Password (not your Gmail account login password).", e.getMessage());
            throw new BadRequestException("Gmail SMTP authentication failed. Please make sure to use a 16-character Google App Password (not your regular account password) in MAIL_PASSWORD.");
        } catch (MessagingException | MailException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new BadRequestException("Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildOtpEmailHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <style>
                        body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 480px; margin: 40px auto; background: #fff; border-radius: 10px; padding: 32px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
                        .logo { font-size: 22px; font-weight: bold; color: #4f46e5; margin-bottom: 24px; }
                        h2 { color: #1f2937; font-size: 20px; margin-bottom: 8px; }
                        p { color: #6b7280; font-size: 15px; line-height: 1.6; }
                        .otp-box { display: block; margin: 28px auto; background: #f0f0ff; border: 2px dashed #4f46e5; border-radius: 8px; padding: 18px 0; text-align: center; font-size: 38px; font-weight: bold; letter-spacing: 12px; color: #4f46e5; }
                        .expiry { color: #ef4444; font-size: 13px; margin-top: 8px; }
                        .footer { margin-top: 32px; font-size: 12px; color: #9ca3af; text-align: center; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="logo">🚀 AI Job Portal</div>
                        <h2>Verify your email address</h2>
                        <p>Use the following One-Time Password (OTP) to verify your account. This code is valid for <strong>10 minutes</strong>.</p>
                        <span class="otp-box">%s</span>
                        <p class="expiry">⚠️ Do not share this OTP with anyone. It expires in 10 minutes.</p>
                        <p>If you did not request this, you can safely ignore this email.</p>
                        <div class="footer">© 2025 AI Job Portal. All rights reserved.</div>
                    </div>
                </body>
                </html>
                """.formatted(otp);
    }
}
