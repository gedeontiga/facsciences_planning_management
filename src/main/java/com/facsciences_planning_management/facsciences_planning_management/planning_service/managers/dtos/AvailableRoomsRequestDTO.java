package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.RoomType;

public record AvailableRoomsRequestDTO(
                LocalTime startTime,
                LocalTime endTime,
                DayOfWeek day,
                LocalDateTime date,
                Long minimumCapacity,
                RoomType roomType) {
}