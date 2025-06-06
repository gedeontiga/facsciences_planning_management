package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.SimpleScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record SimpleSchedulingDTO(
        String id,
        String roomId,
        String ueId,
        String planningId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        String teacherId,
        DayOfWeek day) implements SchedulingDTO {
    public static SimpleSchedulingDTO fromEntity(SimpleScheduling entity) {
        return new SimpleSchedulingDTO(
                entity.getId(),
                entity.getRoom().getId(),
                entity.getUe().getId(),
                entity.getPlanning().getId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getSessionType(),
                entity.getTeacher().getId(),
                entity.getDay());
    }
}