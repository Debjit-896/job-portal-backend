package com.debjitpal.jobportal.job_service.model;

import com.debjitpal.jobportal.domain.ExperienceLevel;
import com.debjitpal.jobportal.domain.JobStatus;
import com.debjitpal.jobportal.domain.JobType;
import com.debjitpal.jobportal.domain.WorkMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID employerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private String requirements;

    @Column(nullable = false, length = 5000)
    private String responsibilities;

    private String benefits;

    @Embedded
    private Location location;

    @Embedded
    private SalaryRange salaryRange;

    private Integer openings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus jobStatus=JobStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;

    private LocalDate applicationDeadline;

    private LocalDate expiredAt;

    @Builder.Default
    private Boolean isActive=true;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;
}