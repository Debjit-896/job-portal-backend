package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String headline;
    private String bio;
    private String phone;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String gender;
    private String website;
    private String linkedinUrl;
    private String githubUrl;
}
