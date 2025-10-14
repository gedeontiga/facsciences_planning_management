package com.facsciencesuy1.planning_management.planning_service.utils.validators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidDateTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateTimeValidator implements ConstraintValidator<ValidDateTime, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}