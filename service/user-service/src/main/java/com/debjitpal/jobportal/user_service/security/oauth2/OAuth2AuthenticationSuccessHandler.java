package com.debjitpal.jobportal.user_service.security.oauth2;

import com.debjitpal.jobportal.user_service.dto.request.OAuthLoginRequest;
import com.debjitpal.jobportal.user_service.dto.response.AuthResponse;
import com.debjitpal.jobportal.user_service.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String defaultRedirectUri;

    public OAuth2AuthenticationSuccessHandler(@Lazy AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect.");
            return;
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = oauthToken.getPrincipal();

            String providerUserId;
            if (oAuth2User instanceof OidcUser oidcUser) {
                providerUserId = oidcUser.getSubject();
            } else if (oAuth2User.getAttribute("sub") != null) {
                providerUserId = oAuth2User.getAttribute("sub").toString();
            } else if (oAuth2User.getAttribute("id") != null) {
                providerUserId = oAuth2User.getAttribute("id").toString();
            } else {
                providerUserId = oAuth2User.getName();
            }

            String email = oAuth2User.getAttribute("email");
            String login = oAuth2User.getAttribute("login");
            if (email == null && login != null) {
                email = login + "@users.noreply.github.com";
            }

            String name = oAuth2User.getAttribute("name");
            if (name == null && login != null) {
                name = login;
            }
            if (name == null) {
                name = "OAuth User";
            }

            OAuthLoginRequest loginRequest = OAuthLoginRequest.builder()
                    .provider(clientRegistrationId)
                    .providerUserId(providerUserId)
                    .email(email)
                    .name(name)
                    .build();

            AuthResponse authResponse = authService.oauthLogin(loginRequest);

            String targetUrl = UriComponentsBuilder.fromUriString(defaultRedirectUri)
                    .queryParam("token", authResponse.getJwt())
                    .queryParam("refreshToken", authResponse.getRefreshToken())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
