package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.domain.UserRole;
import com.debjitpal.jobportal.domain.UserStatus;
import com.debjitpal.jobportal.user_service.dto.AuthResponse;
import com.debjitpal.jobportal.user_service.dto.LoginRequest;
import com.debjitpal.jobportal.user_service.dto.SignupRequest;
import com.debjitpal.jobportal.user_service.mapper.UserMapper;
import com.debjitpal.jobportal.user_service.model.User;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import com.debjitpal.jobportal.user_service.security.CustomUserDetailsService;
import com.debjitpal.jobportal.user_service.security.JwtProvider;
import com.debjitpal.jobportal.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email Already Registered"+request.getEmail());
        }

        if (request.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Admin Registration is not allowed");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .phoneNumber(request.getPhoneNumber())
                .lastLoginAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication, savedUser.getId());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Welcome " + savedUser.getName());
        authResponse.setMessage("User Registered Successfully");
        authResponse.setJwt(jwt);
        authResponse.setUserResponse(UserMapper.toUserResponse(savedUser));
        return authResponse;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticate(
                request.getEmail(), request.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail());
        String jwt = jwtProvider.generateToken(authentication, user.getId());

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Welcome Back " + user.getName());
        authResponse.setMessage("Login Successfully");
        authResponse.setJwt(jwt);
        authResponse.setUserResponse(UserMapper.toUserResponse(user));
        return authResponse;
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (userDetails == null){
            throw new RuntimeException("User not found with email: " + email);
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new RuntimeException("Invalid password for email: " + email);
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }
}
