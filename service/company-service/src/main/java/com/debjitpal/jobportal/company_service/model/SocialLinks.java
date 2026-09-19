package com.debjitpal.jobportal.company_service.model;

import com.debjitpal.jobportal.domain.SocialPlatform;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SocialLinks {

    private SocialPlatform socialPlatformName;
    private String socialPlatformUrl;
}
