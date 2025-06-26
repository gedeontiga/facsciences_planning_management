package com.facsciences_planning_management.facsciences_planning_management.planning_service.components.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.components.AcademicYearValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE }) // Where this can be used
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
@Constraint(validatedBy = AcademicYearValidator.class) // Link to the validator logic
public @interface AcademicYearFormat {

    // Default error message
    String message()

    default "Invalid academic year format. Expected format is YYYY-YYYY where the second year is one greater than the first (e.g., '2025-2026').";

    // Standard validation annotation properties
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
