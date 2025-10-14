package com.facsciencesuy1.planning_management.entities.types;

import java.time.Duration;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TimeSlot {
    @Getter
    @AllArgsConstructor
    public enum CourseTimeSlot {
        COURSE_SLOT_1(LocalTime.of(7, 3), LocalTime.of(9, 55), Duration.ofMinutes(170)),
        COURSE_SLOT_2(LocalTime.of(10, 5), LocalTime.of(12, 55), Duration.ofMinutes(170)),
        COURSE_SLOT_3(LocalTime.of(13, 5), LocalTime.of(15, 55), Duration.ofMinutes(170)),
        COURSE_SLOT_4(LocalTime.of(16, 5), LocalTime.of(18, 55), Duration.ofMinutes(170)),
        COURSE_SLOT_5(LocalTime.of(19, 5), LocalTime.of(21, 55), Duration.ofMinutes(170)),
        TD_SLOT_1(LocalTime.of(8, 0), LocalTime.of(10, 0), Duration.ofHours(2)),
        TD_SLOT_2(LocalTime.of(10, 30), LocalTime.of(12, 30), Duration.ofHours(2)),
        TD_SLOT_3(LocalTime.of(13, 0), LocalTime.of(15, 0), Duration.ofHours(2)),
        TD_SLOT_4(LocalTime.of(15, 30), LocalTime.of(17, 30), Duration.ofHours(2));

        private LocalTime startTime;
        private LocalTime endTime;
        private Duration duration;

        public static CourseTimeSlot fromTimeSlot(LocalTime startTime, LocalTime endTime) {
            for (CourseTimeSlot slot : CourseTimeSlot.values()) {
                if (slot.startTime.equals(startTime) && slot.endTime.equals(endTime)) {
                    return slot;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ExamTimeSlot {
        THREE_HOUR_SLOT_1(LocalTime.of(7, 30), LocalTime.of(10, 30), Duration.ofHours(3)),
        THREE_HOUR_SLOT_2(LocalTime.of(10, 45), LocalTime.of(13, 45), Duration.ofHours(3)),
        THREE_HOUR_SLOT_3(LocalTime.of(14, 0), LocalTime.of(17, 0), Duration.ofHours(3)),
        TWO_HOUR_SLOT_1(LocalTime.of(7, 30), LocalTime.of(9, 30), Duration.ofHours(2)),
        TWO_HOUR_SLOT_2(LocalTime.of(9, 45), LocalTime.of(11, 45), Duration.ofHours(2)),
        TWO_HOUR_SLOT_3(LocalTime.of(12, 0), LocalTime.of(14, 0), Duration.ofHours(2)),
        TWO_HOUR_SLOT_4(LocalTime.of(14, 15), LocalTime.of(16, 15), Duration.ofHours(2)),
        TWO_HOUR_SLOT_5(LocalTime.of(16, 30), LocalTime.of(18, 30), Duration.ofHours(2));

        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Duration duration;

        public static ExamTimeSlot fromTimeSlot(LocalTime startTime, LocalTime endTime) {
            for (ExamTimeSlot slot : ExamTimeSlot.values()) {
                if (slot.startTime.equals(startTime) && slot.endTime.equals(endTime)) {
                    return slot;
                }
            }
            return null;
        }
    }

    /**
     * Gets a CourseTimeSlot by its label name
     * 
     * @param timeSlotLabel the enum name (e.g., "COURSE_SLOT_1")
     * @return the CourseTimeSlot enum instance
     * @throws IllegalArgumentException if the label doesn't correspond to a valid
     *                                  CourseTimeSlot
     */
    public static CourseTimeSlot getCourseTimeSlot(String timeSlotLabel) {
        return CourseTimeSlot.valueOf(timeSlotLabel);
    }

    /**
     * Gets an ExamTimeSlot by its label name
     * 
     * @param timeSlotLabel the enum name (e.g., "THREE_HOUR_SLOT_1")
     * @return the ExamTimeSlot enum instance
     * @throws IllegalArgumentException if the label doesn't correspond to a valid
     *                                  ExamTimeSlot
     */
    public static ExamTimeSlot getExamTimeSlot(String timeSlotLabel) {
        return ExamTimeSlot.valueOf(timeSlotLabel);
    }

    /**
     * Gets either a CourseTimeSlot or ExamTimeSlot by its label name
     * 
     * @param timeSlotLabel the enum name
     * @return the enum instance as Object (cast to appropriate type)
     * @throws IllegalArgumentException if the label doesn't correspond to any valid
     *                                  time slot
     */
    public static Object getTimeSlot(String timeSlotLabel) {
        try {
            return CourseTimeSlot.valueOf(timeSlotLabel);
        } catch (IllegalArgumentException e) {
            return ExamTimeSlot.valueOf(timeSlotLabel);
        }
    }

    /**
     * Gets either a CourseTimeSlot or ExamTimeSlot by its label name with type
     * information
     * 
     * @param timeSlotLabel the enum name
     * @return a TimeSlotResult containing the enum instance and its type
     * @throws IllegalArgumentException if the label doesn't correspond to any valid
     *                                  time slot
     */
    public static TimeSlotResult getTimeSlotWithType(String timeSlotLabel) {
        try {
            CourseTimeSlot courseSlot = CourseTimeSlot.valueOf(timeSlotLabel);
            return new TimeSlotResult(courseSlot, TimeSlotResult.Type.COURSE);
        } catch (IllegalArgumentException e) {
            ExamTimeSlot examSlot = ExamTimeSlot.valueOf(timeSlotLabel);
            return new TimeSlotResult(examSlot, TimeSlotResult.Type.EXAM);
        }
    }

    /**
     * Result wrapper for getTimeSlotWithType method
     */
    public static class TimeSlotResult {
        public enum Type {
            COURSE, EXAM
        }

        private final Object timeSlot;
        private final Type type;

        private TimeSlotResult(Object timeSlot, Type type) {
            this.timeSlot = timeSlot;
            this.type = type;
        }

        public Object getTimeSlot() {
            return timeSlot;
        }

        public Type getType() {
            return type;
        }

        public CourseTimeSlot asCourseTimeSlot() {
            if (type != Type.COURSE) {
                throw new IllegalStateException("TimeSlot is not a CourseTimeSlot");
            }
            return (CourseTimeSlot) timeSlot;
        }

        public ExamTimeSlot asExamTimeSlot() {
            if (type != Type.EXAM) {
                throw new IllegalStateException("TimeSlot is not an ExamTimeSlot");
            }
            return (ExamTimeSlot) timeSlot;
        }

        public boolean isCourseTimeSlot() {
            return type == Type.COURSE;
        }

        public boolean isExamTimeSlot() {
            return type == Type.EXAM;
        }
    }
}
