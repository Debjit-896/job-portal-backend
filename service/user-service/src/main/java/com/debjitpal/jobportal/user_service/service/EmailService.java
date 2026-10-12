package com.debjitpal.jobportal.user_service.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp, String subject);
}
