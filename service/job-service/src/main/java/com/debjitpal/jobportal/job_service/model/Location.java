package com.debjitpal.jobportal.job_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    private String city;
    private String state;
    private String country;
    private String pinCode;
    private String address;
}
