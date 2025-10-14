package com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciencesuy1.planning_management.planning_service.utils.validators.AcademicYearValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AcademicYearValidator.class)
public @interface ValidAcademicYear {
    String message()

    default "Academic year must be in the format 20YY-20YY+1 e.g. 2022-2023";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
