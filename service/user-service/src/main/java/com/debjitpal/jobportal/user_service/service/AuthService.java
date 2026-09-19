package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.dto.AuthResponse;
import com.debjitpal.jobportal.user_service.dto.LoginRequest;
import com.debjitpal.jobportal.user_service.dto.SignupRequest;

public interface AuthService {

    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
