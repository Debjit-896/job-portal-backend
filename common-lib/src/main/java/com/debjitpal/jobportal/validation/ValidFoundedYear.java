package com.debjitpal.jobportal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FoundedYearValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFoundedYear {

    String message() default "Founded year cannot be greater than the current year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
