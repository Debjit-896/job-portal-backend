package com.debjitpal.jobportal.user_service.service;

import com.debjitpal.jobportal.user_service.dto.request.OAuthLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubTokenVerifierService {

    @Value("${spring.security.oauth2.client.registration.github.client-id:}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret:}")
    private String githubClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuthLoginRequest exchangeCodeForUser(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        tokenHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> tokenRequestBody = new HashMap<>();
        tokenRequestBody.put("client_id", githubClientId);
        tokenRequestBody.put("client_secret", githubClientSecret);
        tokenRequestBody.put("code", code);

        HttpEntity<Map<String, String>> tokenRequest = new HttpEntity<>(tokenRequestBody, tokenHeaders);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);
            Map<String, Object> tokenBody = tokenResponse.getBody();

            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                String error = tokenBody != null ? String.valueOf(tokenBody.get("error_description")) : "Unknown error";
                throw new RuntimeException("Failed to exchange GitHub authorization code: " + error);
            }

            String accessToken = (String) tokenBody.get("access_token");

            // Fetch GitHub user profile
            HttpHeaders apiHeaders = new HttpHeaders();
            apiHeaders.setBearerAuth(accessToken);
            apiHeaders.set("User-Agent", "JobPortal-Backend");

            HttpEntity<Void> apiRequest = new HttpEntity<>(apiHeaders);
            @SuppressWarnings("unchecked")
            ResponseEntity<Map> userResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    apiRequest,
                    Map.class
            );

            Map<String, Object> userData = userResponse.getBody();
            if (userData == null || !userData.containsKey("id")) {
                throw new RuntimeException("Failed to fetch GitHub user details");
            }

            String providerUserId = String.valueOf(userData.get("id"));
            String login = (String) userData.get("login");
            String name = (String) userData.get("name");
            if (name == null || name.isBlank()) {
                name = login;
            }

            String email = (String) userData.get("email");
            if (email == null || email.isBlank()) {
                email = fetchPrimaryEmail(accessToken);
            }
            if (email == null || email.isBlank()) {
                email = login + "@users.noreply.github.com";
            }

            return OAuthLoginRequest.builder()
                    .provider("github")
                    .providerUserId(providerUserId)
                    .email(email)
                    .name(name)
                    .build();
        } catch (Exception e) {
            log.error("GitHub OAuth exchange failed: {}", e.getMessage());
            throw new RuntimeException("GitHub OAuth authentication failed: " + e.getMessage(), e);
        }
    }

    private String fetchPrimaryEmail(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("User-Agent", "JobPortal-Backend");

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                for (Map<String, Object> entry : emails) {
                    Boolean primary = (Boolean) entry.get("primary");
                    Boolean verified = (Boolean) entry.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) entry.get("email");
                    }
                }
                if (!emails.isEmpty()) {
                    return (String) emails.get(0).get("email");
                }
            }
        } catch (Exception ex) {
            log.warn("Could not fetch user emails from GitHub API: {}", ex.getMessage());
        }
        return null;
    }
}
