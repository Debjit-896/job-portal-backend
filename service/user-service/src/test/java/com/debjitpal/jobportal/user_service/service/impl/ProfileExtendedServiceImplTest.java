package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.dto.request.CertificationRequest;
import com.debjitpal.jobportal.user_service.dto.response.CertificationResponse;
import com.debjitpal.jobportal.user_service.entity.Certification;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.repository.CertificationRepository;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileExtendedServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @InjectMocks
    private ProfileExtendedServiceImpl profileExtendedService;

    private User testUser;
    private Certification testCert;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).email(email).build();
        testCert = Certification.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .name("AWS Certified")
                .issuingOrganization("AWS")
                .build();
    }

    @Test
    void createCertification_ShouldSaveAndReturnResponse() {
        CertificationRequest request = new CertificationRequest();
        request.setName("AWS Certified");
        request.setIssuingOrganization("AWS");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(certificationRepository.save(any(Certification.class))).thenReturn(testCert);

        CertificationResponse response = profileExtendedService.createCertification(email, request);

        assertNotNull(response);
        assertEquals("AWS Certified", response.getName());
        verify(certificationRepository, times(1)).save(any(Certification.class));
    }

    @Test
    void getCertifications_ShouldReturnListOfCertifications() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(certificationRepository.findByUserId(testUser.getId())).thenReturn(List.of(testCert));

        List<CertificationResponse> list = profileExtendedService.getCertifications(email);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("AWS Certified", list.get(0).getName());
    }
}
