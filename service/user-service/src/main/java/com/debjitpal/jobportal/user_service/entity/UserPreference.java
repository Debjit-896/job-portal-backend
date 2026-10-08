package com.debjitpal.jobportal.user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(length = 50)
    private String preferredJobType;

    @Column(length = 50)
    private String preferredWorkMode;

    @Column(precision = 12, scale = 2)
    private BigDecimal expectedMinSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal expectedMaxSalary;

    private Integer noticePeriodDays;

    private Boolean willingToRelocate;

    @Column(length = 255)
    private String preferredLocation;
    
    @Column(length = 500)
    private String preferredJobTitles;
    
    @Column(length = 500)
    private String preferredSkills;
    
    @Column(length = 50)
    private String experienceLevel;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
