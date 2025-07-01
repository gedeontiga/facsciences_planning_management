package com.facsciences_planning_management.facsciences_planning_management.planning_service.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.AcademicYearFormat;

public class AcademicYearValidator implements ConstraintValidator<AcademicYearFormat, String> {

    // Pre-compile the regex for performance
    private static final Pattern ACADEMIC_YEAR_PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");

    @Override
    public void initialize(AcademicYearFormat constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or blank values are considered valid by this validator.
        // Use @NotNull or @NotBlank for that purpose.
        if (value == null || value.isBlank()) {
            return true;
        }

        // 1. Check the basic format "YYYY-YYYY"
        if (!ACADEMIC_YEAR_PATTERN.matcher(value).matches()) {
            return false;
        }

        try {
            // 2. Split and parse the years
            String[] years = value.split("-");
            int startYear = Integer.parseInt(years[0]);
            int endYear = Integer.parseInt(years[1]);

            // 3. Check if the second year is exactly one greater than the first
            return endYear == startYear + 1;
        } catch (NumberFormatException e) {
            // This should not happen if the regex matches, but it's good practice
            return false;
        }
    }
}