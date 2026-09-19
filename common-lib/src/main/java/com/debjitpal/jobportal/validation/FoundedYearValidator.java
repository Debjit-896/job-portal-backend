package com.debjitpal.jobportal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class FoundedYearValidator
        implements ConstraintValidator<ValidFoundedYear, Integer> {

    @Override
    public boolean isValid(Integer foundedYear,
                           ConstraintValidatorContext context) {

        if (foundedYear == null) {
            return true;
        }

        return foundedYear <= Year.now().getValue();
    }
}