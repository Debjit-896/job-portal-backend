package com.debjitpal.jobportal.user_service.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfo {
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
}
