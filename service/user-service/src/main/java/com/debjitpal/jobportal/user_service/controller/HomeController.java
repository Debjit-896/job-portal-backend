package com.debjitpal.jobportal.user_service.controller;

import com.debjitpal.jobportal.domain.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the User Service!"+ UserRole.APPLICANT;
    }
}
