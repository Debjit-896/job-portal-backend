package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.domain.UserRole;
import com.debjitpal.jobportal.domain.UserStatus;
import com.debjitpal.jobportal.user_service.mapper.UserMapper;
import com.debjitpal.jobportal.user_service.entity.OAuthAccount;
import com.debjitpal.jobportal.user_service.entity.UserProfile;
import com.debjitpal.jobportal.user_service.repository.OAuthAccountRepository;
import com.debjitpal.jobportal.user_service.repository.UserProfileRepository;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import com.debjitpal.jobportal.user_service.security.CustomUserDetailsService;
import com.debjitpal.jobportal.user_service.security.JwtProvider;
import com.debjitpal.jobportal.user_service.service.AuthService;
import com.debjitpal.jobportal.user_service.service.TokenService;
import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
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
    private final UserProfileRepository userProfileRepository;
    private final OAuthAccountRepository oauthAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email Already Registered"+request.getEmail());
        }

        UserRole assignedRole = request.getRole() != null ? request.getRole() : UserRole.JOB_SEEKER;
        if (assignedRole == UserRole.ADMIN) {
            throw new RuntimeException("Admin Registration is not allowed");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .build();
        userProfileRepository.save(profile);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication, savedUser.getId());
        String refreshToken = tokenService.generateRefreshToken(savedUser, "Unknown Device", "Unknown IP", "Unknown User Agent");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Welcome " + savedUser.getName());
        authResponse.setMessage("User Registered Successfully");
        authResponse.setJwt(jwt);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setUserResponse(UserMapper.toUserResponse(savedUser));
        return authResponse;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticate(
                request.getEmail(), request.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));
        String jwt = jwtProvider.generateToken(authentication, user.getId());
        String refreshToken = tokenService.generateRefreshToken(user, "Unknown Device", "Unknown IP", "Unknown User Agent");

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Welcome Back " + user.getName());
        authResponse.setMessage("Login Successfully");
        authResponse.setJwt(jwt);
        authResponse.setRefreshToken(refreshToken);
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

    @Override
    public AuthResponse oauthLogin(OAuthLoginRequest request) {
        OAuthAccount oauthAccount = oauthAccountRepository
                .findByProviderAndProviderUserId(request.getProvider(), request.getProviderUserId())
                .orElse(null);

        User user;
        if (oauthAccount != null) {
            user = oauthAccount.getUser();
        } else {
            // Prevent automatic account merging based on email
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("An account with this email already exists. Please log in with your credentials and link this OAuth provider manually.");
            }

            user = User.builder()
                    .name(request.getName() != null ? request.getName() : "OAuth User")
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                    .role(UserRole.JOB_SEEKER)
                    .status(UserStatus.ACTIVE)
                    .emailVerified(true)
                    .lastLoginAt(LocalDateTime.now())
                    .build();
            user = userRepository.save(user);

            UserProfile profile = UserProfile.builder()
                    .user(user)
                    .build();
            userProfileRepository.save(profile);

            OAuthAccount newAccount = OAuthAccount.builder()
                    .user(user)
                    .provider(request.getProvider())
                    .providerUserId(request.getProviderUserId())
                    .email(request.getEmail())
                    .build();
            oauthAccountRepository.save(newAccount);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication, user.getId());
        String refreshToken = tokenService.generateRefreshToken(user, "Unknown Device", "Unknown IP", "Unknown User Agent");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Welcome " + user.getName());
        authResponse.setMessage("Login Successfully");
        authResponse.setJwt(jwt);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setUserResponse(UserMapper.toUserResponse(user));
        return authResponse;
    }

    @Override
    public AuthResponse linkOAuthAccount(LinkOAuthRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        boolean exists = oauthAccountRepository.findByProviderAndProviderUserId(request.getProvider(), request.getProviderUserId()).isPresent();
        if (exists) {
            throw new RuntimeException("This OAuth account is already linked to a user.");
        }

        OAuthAccount newAccount = OAuthAccount.builder()
                .user(user)
                .provider(request.getProvider())
                .providerUserId(request.getProviderUserId())
                .email(request.getEmail())
                .build();
        oauthAccountRepository.save(newAccount);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Account Linked");
        authResponse.setMessage("OAuth account linked successfully");
        authResponse.setUserResponse(UserMapper.toUserResponse(user));
        return authResponse;
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken token = tokenService.validateRefreshToken(request.getRefreshToken());
        User user = token.getUser();
        
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());
                
        String newJwt = jwtProvider.generateToken(authentication, user.getId());
        // For security, rotate refresh token
        tokenService.revokeRefreshToken(request.getRefreshToken());
        String newRefreshToken = tokenService.generateRefreshToken(user, "Unknown Device", "Unknown IP", "Unknown User Agent");
        
        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Token Refreshed");
        authResponse.setMessage("Token successfully refreshed");
        authResponse.setJwt(newJwt);
        authResponse.setRefreshToken(newRefreshToken);
        authResponse.setUserResponse(UserMapper.toUserResponse(user));
        return authResponse;
    }

    @Override
    public void logout(LogoutRequest request) {
        tokenService.revokeRefreshToken(request.getRefreshToken());
    }

    @Override
    public void logoutAll(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        tokenService.revokeAllRefreshTokensForUser(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        // Find user by email, ignore if not found to prevent email enumeration
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String resetToken = tokenService.generatePasswordResetToken(user);
            // TODO: send resetToken to user via email
            System.out.println("Generated reset token for " + user.getEmail() + ": " + resetToken);
        });
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        com.debjitpal.jobportal.user_service.entity.PasswordResetToken token = tokenService.validatePasswordResetToken(request.getToken());
        User user = token.getUser();
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        tokenService.consumePasswordResetToken(request.getToken());
        tokenService.revokeAllRefreshTokensForUser(user); // Force re-login on all devices
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse me(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserResponse(user);
    }

    @Override
    public void sendEmailVerification(EmailVerificationRequest request) {
        // Find user by email, ignore if not found or already verified to prevent email enumeration
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (!user.isEmailVerified()) {
                String verificationToken = tokenService.generateEmailVerificationToken(user);
                // TODO: send verificationToken to user via email
                System.out.println("Generated verification token for " + user.getEmail() + ": " + verificationToken);
            }
        });
    }

    @Override
    public void confirmEmail(ConfirmEmailRequest request) {
        com.debjitpal.jobportal.user_service.entity.EmailVerificationToken token = tokenService.validateEmailVerificationToken(request.getToken());
        User user = token.getUser();
        
        user.setEmailVerified(true);
        userRepository.save(user);
        
        tokenService.consumeEmailVerificationToken(request.getToken());
    }

    @Override
    public java.util.List<LinkedOAuthAccountResponse> getLinkedOAuthAccounts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return oauthAccountRepository.findByUserId(user.getId()).stream()
                .map(account -> LinkedOAuthAccountResponse.builder()
                        .provider(account.getProvider())
                        .email(account.getEmail())
                        .linkedAt(account.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public void unlinkOAuthAccount(String provider, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        OAuthAccount account = oauthAccountRepository.findByUserIdAndProvider(user.getId(), provider)
                .orElseThrow(() -> new RuntimeException("OAuth account not found for provider: " + provider));
        
        // Prevent unlinking if it's the only way to login (no password set)
        if ((user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().equals(passwordEncoder.encode(""))) 
                && oauthAccountRepository.findByUserId(user.getId()).size() <= 1) {
            throw new RuntimeException("Cannot unlink the only authentication method for this account. Please set a password first.");
        }
        
        oauthAccountRepository.delete(account);
    }
}

