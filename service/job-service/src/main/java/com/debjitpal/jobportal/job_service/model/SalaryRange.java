package com.debjitpal.jobportal.job_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SalaryRange {
    private BigDecimal minimumSalary;
    private BigDecimal maximumSalary;
}
