package com.debjitpal.jobportal.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedOAuthAccountResponse {
    private String provider;
    private String email;
    private LocalDateTime linkedAt;
}
