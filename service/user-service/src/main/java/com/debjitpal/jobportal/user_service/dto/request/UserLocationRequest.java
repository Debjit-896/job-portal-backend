package com.debjitpal.jobportal.user_service.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserLocationRequest {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
