package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public record ReservationRequestDTO(
        String teacherId,
        SessionType sessionType,
        String roomId,
        LocalTime startTime,
        LocalTime endTime,
        DayOfWeek day,
        LocalDateTime date,
        String timetableId) {
}
