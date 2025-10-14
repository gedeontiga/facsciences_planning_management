package com.facsciencesuy1.planning_management.planning_service.utils.validators;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeValidator implements ConstraintValidator<ValidTime, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true; // Let @NotNull handle null validation
        try {
            LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}