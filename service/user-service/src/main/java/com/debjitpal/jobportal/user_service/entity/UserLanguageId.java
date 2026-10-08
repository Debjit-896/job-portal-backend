package com.debjitpal.jobportal.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserLanguageId implements Serializable {
    private UUID userId;
    private UUID languageId;
}
