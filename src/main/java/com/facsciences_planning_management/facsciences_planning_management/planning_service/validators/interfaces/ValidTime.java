package com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.TimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeValidator.class)
@Documented
public @interface ValidTime {
    String message() default "Invalid time format. Expected HH:mm";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
