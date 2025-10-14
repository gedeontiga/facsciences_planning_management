package com.facsciencesuy1.planning_management.planning_service.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.facsciencesuy1.planning_management.entities.types.TimeSlot;

@Target({ ElementType.FIELD, ElementType.PARAMETER,
        ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeSlot.TimeSlotValidator.class)
public @interface ValidTimeSlot {

    String message() default "Invalid time slot label";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Specify which type of time slots to validate against
     */
    TimeSlotType value() default TimeSlotType.ANY;

    enum TimeSlotType {
        COURSE_ONLY,
        EXAM_ONLY,
        ANY
    }

    class TimeSlotValidator implements ConstraintValidator<ValidTimeSlot, String> {

        private TimeSlotType timeSlotType;

        @Override
        public void initialize(ValidTimeSlot constraintAnnotation) {
            this.timeSlotType = constraintAnnotation.value();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // Let @NotNull handle null validation
            }

            return switch (timeSlotType) {
                case COURSE_ONLY -> isValidCourseTimeSlot(value);
                case EXAM_ONLY -> isValidExamTimeSlot(value);
                case ANY -> isValidCourseTimeSlot(value) || isValidExamTimeSlot(value);
            };
        }

        private boolean isValidCourseTimeSlot(String timeSlotLabel) {
            try {
                TimeSlot.CourseTimeSlot.valueOf(timeSlotLabel);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        private boolean isValidExamTimeSlot(String timeSlotLabel) {
            try {
                TimeSlot.ExamTimeSlot.valueOf(timeSlotLabel);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
