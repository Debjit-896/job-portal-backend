package com.debjitpal.jobportal.user_service.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String name;
    private String phoneNumber;
    private String profileImage;
}
