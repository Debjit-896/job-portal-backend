package com.debjitpal.jobportal.dto.response;

import com.debjitpal.jobportal.domain.SocialPlatform;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SocialLinksResponse {

    private SocialPlatform socialPlatformName;
    private String socialPlatformUrl;
}
