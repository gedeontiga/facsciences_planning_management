package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record SimpleSchedulingUpdateRequest(
        String roomId,
        String ueId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        String teacherId,
        DayOfWeek day) {
}