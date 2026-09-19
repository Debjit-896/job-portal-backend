package com.debjitpal.jobportal.company_service.controller;

import com.debjitpal.jobportal.domain.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/company-service")
    public String home() {
        return "Welcome to the Company Service! "+ UserRole.EMPLOYER;
    }
}
