package com.facsciences_planning_management.facsciences_planning_management.planning_service.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.AcademicYearFormat;

public class AcademicYearValidator implements ConstraintValidator<AcademicYearFormat, String> {
    private static final Pattern ACADEMIC_YEAR_PATTERN = Pattern.compile("^20\\d{2}-20\\d{2}$");

    @Override
    public void initialize(AcademicYearFormat constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false; // Changed: null/blank should not be valid for academic year
        }

        if (!ACADEMIC_YEAR_PATTERN.matcher(value).matches()) {
            return false;
        }

        try {
            String[] years = value.split("-");
            int startYear = Integer.parseInt(years[0]);
            int endYear = Integer.parseInt(years[1]);

            // Ensure both years are in 20xx format and end year is exactly start year + 1
            return startYear >= 2000 && startYear <= 2099 &&
                    endYear >= 2000 && endYear <= 2099 &&
                    endYear == startYear + 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}