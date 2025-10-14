package com.facsciencesuy1.planning_management.planning_service.utils.validators;

import java.time.LocalDate;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidDate;

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
