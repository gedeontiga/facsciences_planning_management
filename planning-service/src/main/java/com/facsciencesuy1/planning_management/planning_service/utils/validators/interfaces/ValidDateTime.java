package com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.DateTimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
@Documented
public @interface ValidDateTime {
    String message() default "Invalid datetime format. Expected yyyy-MM-ddTHH:mm:ss";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}