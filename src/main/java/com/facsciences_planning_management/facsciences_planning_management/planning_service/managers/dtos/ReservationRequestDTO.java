package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

@Data
@Builder
public final class ReservationRequestDTO {
    private String teacherId;
    private SessionType sessionType;
    private String roomId;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek day;
    private LocalDateTime date;
    private String timetableId;
}
