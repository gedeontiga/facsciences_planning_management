package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.Duration;
import java.time.LocalTime;

public record TimeSlotDTO(
        LocalTime startTime,
        LocalTime endTime,
        Duration duration,
        String label) {
}
