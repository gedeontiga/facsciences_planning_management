package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record SimpleSchedulingCreateRequest(
        String roomId,
        String ueId,
        String planningId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        String teacherId,
        DayOfWeek day) {
}