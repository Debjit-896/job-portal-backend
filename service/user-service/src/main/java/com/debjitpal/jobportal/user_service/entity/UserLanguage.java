package com.debjitpal.jobportal.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLanguage {
    @EmbeddedId
    private UserLanguageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("languageId")
    @JoinColumn(name = "language_id")
    private Language language;

    @Column(length = 30)
    private String proficiency;
}
