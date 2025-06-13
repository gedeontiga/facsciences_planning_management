package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record CourseSchedulingDTO(
        String id,
        String roomId,
        String roomCode,
        String ueId,
        String ueName,
        String timetableId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        String teacherId,
        String teacherFirstName,
        String teacherLastName,
        DayOfWeek day) implements SchedulingDTO {
    public static CourseSchedulingDTO fromEntity(CourseScheduling entity) {
        return new CourseSchedulingDTO(
                entity.getId(),
                entity.getRoom().getId(),
                entity.getRoom().getCode(),
                entity.getAssignedCourse().getUe().getId(),
                entity.getAssignedCourse().getUe().getName(),
                entity.getTimetable().getId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getSessionType(),
                entity.getAssignedCourse().getTeacher().getId(),
                entity.getAssignedCourse().getTeacher().getFirstName(),
                entity.getAssignedCourse().getTeacher().getLastName(),
                entity.getDay());
    }
}