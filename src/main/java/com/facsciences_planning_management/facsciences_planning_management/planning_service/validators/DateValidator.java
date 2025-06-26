package com.facsciences_planning_management.facsciences_planning_management.planning_service.validators;

import java.time.LocalDate;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
