package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types;

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
}
