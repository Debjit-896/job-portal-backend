package com.debjitpal.jobportal.user_service.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ClientInfoResolver {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * Resolves client info from current RequestContextHolder attributes.
     */
    public ClientInfo getClientInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes.getRequest() != null) {
            return resolveClientInfo(attributes.getRequest());
        }
        return ClientInfo.builder()
                .deviceInfo("Unknown Device")
                .ipAddress("Unknown IP")
                .userAgent("Unknown User Agent")
                .build();
    }

    /**
     * Resolves client info from a specific HttpServletRequest.
     */
    public ClientInfo resolveClientInfo(HttpServletRequest request) {
        String ipAddress = resolveClientIp(request);
        String userAgent = resolveUserAgent(request);
        String deviceInfo = resolveDeviceInfo(request, userAgent);

        return ClientInfo.builder()
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "Unknown IP";
        }

        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (StringUtils.hasText(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
                // X-Forwarded-For may contain a comma-separated list of IPs: client, proxy1, proxy2
                String clientIp = ipList.split(",")[0].trim();
                return normalizeIp(clientIp);
            }
        }

        String remoteAddr = request.getRemoteAddr();
        if (StringUtils.hasText(remoteAddr)) {
            return normalizeIp(remoteAddr.trim());
        }

        return "Unknown IP";
    }

    private String normalizeIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return "Unknown IP";
        }
        // Normalize IPv6 localhost loopback
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        // PostgreSQL column length for ip_address is 45 characters
        if (ip.length() > 45) {
            return ip.substring(0, 45);
        }
        return ip;
    }

    public String resolveUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "Unknown User Agent";
        }
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.hasText(userAgent)) {
            return userAgent.trim();
        }
        return "Unknown User Agent";
    }

    public String resolveDeviceInfo(HttpServletRequest request, String userAgent) {
        // 1. Check for custom header if supplied by client application (e.g., mobile apps / frontend)
        if (request != null) {
            String explicitDevice = request.getHeader("X-Device-Info");
            if (StringUtils.hasText(explicitDevice)) {
                return truncate(explicitDevice.trim(), 255);
            }
        }

        if (!StringUtils.hasText(userAgent) || "Unknown User Agent".equalsIgnoreCase(userAgent)) {
            return "Unknown Device";
        }

        String ua = userAgent.trim();

        // 2. Identify common API clients and development tools
        if (ua.contains("PostmanRuntime")) {
            return "Postman";
        }
        if (ua.contains("Thunder Client")) {
            return "Thunder Client";
        }
        if (ua.contains("insomnia")) {
            return "Insomnia";
        }
        if (ua.startsWith("curl/")) {
            return "cURL";
        }

        // 3. Detect Operating System
        String os = null;
        if (ua.contains("Windows NT 10.0")) {
            os = "Windows 10/11";
        } else if (ua.contains("Windows NT 6.3")) {
            os = "Windows 8.1";
        } else if (ua.contains("Windows NT 6.2")) {
            os = "Windows 8";
        } else if (ua.contains("Windows NT 6.1")) {
            os = "Windows 7";
        } else if (ua.contains("Windows")) {
            os = "Windows";
        } else if (ua.contains("iPhone")) {
            os = "iOS (iPhone)";
        } else if (ua.contains("iPad")) {
            os = "iPadOS";
        } else if (ua.contains("Android")) {
            os = "Android";
        } else if (ua.contains("Macintosh") || ua.contains("Mac OS X")) {
            os = "macOS";
        } else if (ua.contains("Linux")) {
            os = "Linux";
        } else if (ua.contains("CrOS")) {
            os = "Chrome OS";
        }

        // 4. Detect Browser
        String browser = null;
        if (ua.contains("Edg/")) {
            browser = "Edge";
        } else if (ua.contains("OPR/") || ua.contains("Opera")) {
            browser = "Opera";
        } else if (ua.contains("Chrome/") && !ua.contains("Chromium")) {
            browser = "Chrome";
        } else if (ua.contains("Safari/") && !ua.contains("Chrome/")) {
            browser = "Safari";
        } else if (ua.contains("Firefox/")) {
            browser = "Firefox";
        }

        // 5. Build human-readable device string
        if (browser != null && os != null) {
            return browser + " on " + os;
        } else if (browser != null) {
            return browser;
        } else if (os != null) {
            return os + " Device";
        }

        return truncate(ua, 255);
    }

    private String truncate(String str, int maxLen) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLen ? str.substring(0, maxLen) : str;
    }
}
