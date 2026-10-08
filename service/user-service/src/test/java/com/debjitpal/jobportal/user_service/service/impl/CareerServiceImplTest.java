package com.debjitpal.jobportal.user_service.service.impl;

import com.debjitpal.jobportal.user_service.dto.request.EducationRequest;
import com.debjitpal.jobportal.user_service.dto.response.EducationResponse;
import com.debjitpal.jobportal.user_service.entity.Education;
import com.debjitpal.jobportal.user_service.entity.User;
import com.debjitpal.jobportal.user_service.repository.EducationRepository;
import com.debjitpal.jobportal.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CareerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EducationRepository educationRepository;

    @InjectMocks
    private CareerServiceImpl careerService;

    private User testUser;
    private Education testEducation;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).email(email).build();
        testEducation = Education.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .institution("MIT")
                .degree("B.Sc")
                .build();
    }

    @Test
    void createEducation_ShouldSaveAndReturnResponse() {
        EducationRequest request = new EducationRequest();
        request.setInstitution("MIT");
        request.setDegree("B.Sc");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(educationRepository.save(any(Education.class))).thenReturn(testEducation);

        EducationResponse response = careerService.createEducation(email, request);

        assertNotNull(response);
        assertEquals("MIT", response.getInstitution());
        assertEquals("B.Sc", response.getDegree());
        verify(educationRepository, times(1)).save(any(Education.class));
    }

    @Test
    void getEducations_ShouldReturnListOfEducations() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(educationRepository.findByUserId(testUser.getId())).thenReturn(List.of(testEducation));

        List<EducationResponse> list = careerService.getEducations(email);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("MIT", list.get(0).getInstitution());
    }
}
