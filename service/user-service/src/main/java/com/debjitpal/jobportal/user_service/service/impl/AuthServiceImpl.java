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
import com.debjitpal.jobportal.user_service.service.EmailService;
import com.debjitpal.jobportal.user_service.service.TokenService;
import com.debjitpal.jobportal.user_service.service.GoogleTokenVerifierService;
import com.debjitpal.jobportal.user_service.service.GithubTokenVerifierService;
import com.debjitpal.jobportal.user_service.util.ClientInfo;
import com.debjitpal.jobportal.user_service.util.ClientInfoResolver;
import com.debjitpal.jobportal.user_service.entity.RefreshToken;
import com.debjitpal.jobportal.dto.response.ApiResponse;
import com.debjitpal.jobportal.dto.response.UserResponse;
import com.debjitpal.jobportal.user_service.dto.request.*;
import com.debjitpal.jobportal.user_service.dto.response.*;
import com.debjitpal.jobportal.user_service.exception.BadRequestException;
import com.debjitpal.jobportal.user_service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
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
    private final EmailService emailService;
    private final ClientInfoResolver clientInfoResolver;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final GithubTokenVerifierService githubTokenVerifierService;

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
                .phone(request.getPhone())
                .build();
        userProfileRepository.save(profile);

        // Automatically dispatch verification OTP to user's registered email
        try {
            tokenService.generateEmailVerificationOtp(savedUser);
        } catch (Exception e) {
            log.warn("Failed to send initial email verification OTP during signup to {}: {}", savedUser.getEmail(), e.getMessage());
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ClientInfo clientInfo = clientInfoResolver.getClientInfo();
        String jwt = jwtProvider.generateToken(authentication, savedUser.getId());
        String refreshToken = tokenService.generateRefreshToken(savedUser, clientInfo.getDeviceInfo(), clientInfo.getIpAddress(), clientInfo.getUserAgent());

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
        ClientInfo clientInfo = clientInfoResolver.getClientInfo();
        String jwt = jwtProvider.generateToken(authentication, user.getId());
        String refreshToken = tokenService.generateRefreshToken(user, clientInfo.getDeviceInfo(), clientInfo.getIpAddress(), clientInfo.getUserAgent());

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
        ClientInfo clientInfo = clientInfoResolver.getClientInfo();
        String jwt = jwtProvider.generateToken(authentication, user.getId());
        String refreshToken = tokenService.generateRefreshToken(user, clientInfo.getDeviceInfo(), clientInfo.getIpAddress(), clientInfo.getUserAgent());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTitle("Welcome " + user.getName());
        authResponse.setMessage("Login Successfully");
        authResponse.setJwt(jwt);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setUserResponse(UserMapper.toUserResponse(user));
        return authResponse;
    }

    @Override
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        OAuthLoginRequest oauthRequest = googleTokenVerifierService.verifyGoogleIdToken(request.getIdToken());
        return oauthLogin(oauthRequest);
    }

    @Override
    public AuthResponse githubLogin(GithubLoginRequest request) {
        OAuthLoginRequest oauthRequest = githubTokenVerifierService.exchangeCodeForUser(request.getCode());
        return oauthLogin(oauthRequest);
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
        ClientInfo clientInfo = clientInfoResolver.getClientInfo();
        // For security, rotate refresh token
        tokenService.revokeRefreshToken(request.getRefreshToken());
        String newRefreshToken = tokenService.generateRefreshToken(user, clientInfo.getDeviceInfo(), clientInfo.getIpAddress(), clientInfo.getUserAgent());
        
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
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Find user by email, ignore if not found to prevent email enumeration
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String resetOtp = tokenService.generatePasswordResetOtp(user);
            emailService.sendOtpEmail(
                    user.getEmail(),
                    resetOtp,
                    "Your AI Job Portal Password Reset OTP"
            );
            log.info("Password reset OTP sent to {}", user.getEmail());
        });
    }

    @Override
    public VerifyResetOtpResponse verifyResetOtp(VerifyResetOtpRequest request) {
        String resetToken = tokenService.verifyPasswordResetOtp(request.getEmail(), request.getOtp());
        return VerifyResetOtpResponse.builder()
                .resetToken(resetToken)
                .message("OTP verified successfully")
                .build();
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user;
        if (request.getToken() != null && !request.getToken().isBlank()) {
            com.debjitpal.jobportal.user_service.entity.PasswordResetToken token = tokenService.validatePasswordResetToken(request.getToken());
            user = token.getUser();
            tokenService.consumePasswordResetToken(request.getToken());
        } else if (request.getEmail() != null && request.getOtp() != null) {
            com.debjitpal.jobportal.user_service.entity.PasswordResetToken token = tokenService.validatePasswordResetOtp(request.getEmail(), request.getOtp());
            user = token.getUser();
            tokenService.consumePasswordResetOtp(request.getEmail(), request.getOtp());
        } else {
            throw new RuntimeException("Reset token or email with OTP is required");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
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
    @Transactional
    public void sendEmailVerification(EmailVerificationRequest request) {
        String targetEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + targetEmail));
        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }
        tokenService.generateEmailVerificationOtp(user);
        log.info("Email verification OTP requested and dispatched to {}", targetEmail);
    }

    @Override
    @Transactional
    public ApiResponse confirmEmail(ConfirmEmailRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        String otpCode = request.getEffectiveOtp();
        if (otpCode == null || otpCode.isBlank()) {
            throw new BadRequestException("Verification OTP is required");
        }

        com.debjitpal.jobportal.user_service.entity.EmailVerificationToken token;
        if (email != null && !email.isBlank()) {
            token = tokenService.validateEmailVerificationOtp(email, otpCode);
        } else {
            token = tokenService.validateEmailVerificationToken(otpCode);
        }

        // Eagerly fetch and update User within this active transaction to avoid LazyInitializationException
        User user = token.getUser();
        if (user == null && email != null) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        } else if (user != null) {
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else {
            throw new ResourceNotFoundException("User associated with verification token not found");
        }

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // Consume (delete) the token so it is removed from the DB permanently after use
        if (email != null && !email.isBlank()) {
            tokenService.consumeEmailVerificationOtp(email, otpCode);
        } else {
            tokenService.consumeEmailVerificationToken(otpCode);
        }

        log.info("Email verified successfully for user: {}", user.getEmail());
        return new ApiResponse(true, "Email verified successfully");
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

