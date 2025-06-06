package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record SchedulingSummaryDTO(
        String schedulingId,
        String roomName,
        String ueName,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType) {
}