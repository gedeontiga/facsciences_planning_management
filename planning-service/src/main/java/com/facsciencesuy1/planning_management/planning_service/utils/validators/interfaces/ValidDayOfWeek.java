package com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.DayOfWeekValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DayOfWeekValidator.class)
@Documented
public @interface ValidDayOfWeek {
    String message() default "Invalid day of week. Expected MONDAY, TUESDAY, etc.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}