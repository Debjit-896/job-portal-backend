package com.debjitpal.jobportal.user_service.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientInfoResolverTest {

    private ClientInfoResolver resolver;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        resolver = new ClientInfoResolver();
    }

    @Test
    void resolveClientIp_FromXForwardedForMultipleIps() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18, 150.172.238.178");
        String ip = resolver.resolveClientIp(request);
        assertEquals("203.0.113.195", ip);
    }

    @Test
    void resolveClientIp_NormalizeIpv6Loopback() {
        when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");
        String ip = resolver.resolveClientIp(request);
        assertEquals("127.0.0.1", ip);
    }

    @Test
    void resolveClientIp_NormalizeShortIpv6Loopback() {
        when(request.getRemoteAddr()).thenReturn("::1");
        String ip = resolver.resolveClientIp(request);
        assertEquals("127.0.0.1", ip);
    }

    @Test
    void resolveClientIp_FallbackRemoteAddr() {
        when(request.getRemoteAddr()).thenReturn("192.168.1.50");
        String ip = resolver.resolveClientIp(request);
        assertEquals("192.168.1.50", ip);
    }

    @Test
    void resolveDeviceInfo_PostmanRuntime() {
        String ua = "PostmanRuntime/7.43.0";
        String device = resolver.resolveDeviceInfo(request, ua);
        assertEquals("Postman", device);
    }

    @Test
    void resolveDeviceInfo_ChromeOnWindows() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        String device = resolver.resolveDeviceInfo(request, ua);
        assertEquals("Chrome on Windows 10/11", device);
    }

    @Test
    void resolveDeviceInfo_SafariOnIPhone() {
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Mobile/15E148 Safari/604.1";
        String device = resolver.resolveDeviceInfo(request, ua);
        assertEquals("Safari on iOS (iPhone)", device);
    }

    @Test
    void resolveDeviceInfo_ExplicitHeaderTakesPrecedence() {
        when(request.getHeader("X-Device-Info")).thenReturn("Google Pixel 8 Pro");
        String device = resolver.resolveDeviceInfo(request, "Some Generic User Agent");
        assertEquals("Google Pixel 8 Pro", device);
    }

    @Test
    void resolveClientInfo_FullRequest() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("103.21.244.2");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0");

        ClientInfo clientInfo = resolver.resolveClientInfo(request);

        assertEquals("103.21.244.2", clientInfo.getIpAddress());
        assertEquals("Firefox on Windows 10/11", clientInfo.getDeviceInfo());
        assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0", clientInfo.getUserAgent());
    }
}
