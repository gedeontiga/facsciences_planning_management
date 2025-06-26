package com.facsciences_planning_management.facsciences_planning_management.planning_service.validators;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Validator implementations
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