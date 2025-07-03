package com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.AcademicYearValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AcademicYearValidator.class)
public @interface AcademicYearFormat {
    String message() default "Invalid academic year format. Expected format is 20XX-20XX where the second year is one greater than the first (e.g., '2025-2026').";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
