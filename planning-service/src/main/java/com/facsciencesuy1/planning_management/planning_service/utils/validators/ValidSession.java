package com.facsciencesuy1.planning_management.planning_service.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSession.SessionValidator.class)
public @interface ValidSession {

    String message() default "Session must be a valid day of week or date (yyyy-MM-dd)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SessionValidator implements ConstraintValidator<ValidSession, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // Let @NotNull handle null validation
            }

            // Check if it's a valid day of week (case-insensitive)
            if (isValidDayOfWeek(value)) {
                return true;
            }

            // Check if it's a valid LocalDate (yyyy-MM-dd format)
            return isValidDate(value);
        }

        private boolean isValidDayOfWeek(String value) {
            try {
                DayOfWeek.valueOf(value.toUpperCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        private boolean isValidDate(String value) {
            try {
                LocalDate.parse(value);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
    }
}
