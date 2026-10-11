package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.dto.request.OAuthLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTokenVerifierService {

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuthLoginRequest verifyGoogleIdToken(String idToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body == null || body.containsKey("error")) {
                throw new RuntimeException("Invalid Google ID token");
            }

            String aud = (String) body.get("aud");
            if (googleClientId != null && !googleClientId.isBlank() && !googleClientId.equals(aud)) {
                log.error("Google ID Token aud mismatch: expected {}, received {}", googleClientId, aud);
                throw new RuntimeException("Google ID Token audience mismatch");
            }

            String emailVerified = String.valueOf(body.get("email_verified"));
            if (!"true".equalsIgnoreCase(emailVerified)) {
                throw new RuntimeException("Google email is not verified");
            }

            String sub = (String) body.get("sub");
            String email = (String) body.get("email");
            String name = (String) body.get("name");

            return OAuthLoginRequest.builder()
                    .provider("google")
                    .providerUserId(sub)
                    .email(email)
                    .name(name != null ? name : "Google User")
                    .build();
        } catch (Exception e) {
            log.error("Failed to verify Google ID token: {}", e.getMessage());
            throw new RuntimeException("Invalid Google ID token: " + e.getMessage(), e);
        }
    }
}
