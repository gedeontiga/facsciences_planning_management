package com.facsciencesuy1.planning_management.planning_service.utils.validators;

import java.time.DayOfWeek;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidDayOfWeek;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DayOfWeekValidator implements ConstraintValidator<ValidDayOfWeek, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        try {
            DayOfWeek.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}