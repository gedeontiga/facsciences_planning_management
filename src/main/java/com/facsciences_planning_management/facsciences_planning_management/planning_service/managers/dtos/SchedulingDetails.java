package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record SchedulingDetails(
        String roomId,
        String roomCode,
        String timetableId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        Optional<String> teacherOrProctorId,
        Optional<DayOfWeek> day,
        Optional<LocalDateTime> sessionDate) {
    public static SchedulingDetails fromEntity(CourseScheduling entity) {
        return new SchedulingDetails(
                entity.getRoom().getId(), entity.getRoom().getCode(), entity.getTimetable().getId(),
                entity.getStartTime(), entity.getEndTime(), entity.getSessionType(),
                Optional.of(entity.getAssignedCourse().getTeacher().getId()),
                Optional.of(entity.getDay()), Optional.empty());
    }

    public static SchedulingDetails fromEntity(ExamScheduling entity) {
        return new SchedulingDetails(
                entity.getRoom().getId(), entity.getRoom().getCode(), entity.getTimetable().getId(),
                entity.getStartTime(), entity.getEndTime(), entity.getSessionType(),
                Optional.of(entity.getProctor().getId()), Optional.empty(),
                Optional.of(entity.getSessionDate()));
    }
}