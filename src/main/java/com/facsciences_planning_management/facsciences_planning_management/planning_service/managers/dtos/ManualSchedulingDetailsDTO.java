package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

@Data
@Builder
public class ManualSchedulingDetailsDTO {
    private String roomId;
    private String timetableId;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek day;
    private LocalDateTime sessionDate;
    private SessionType sessionType;

    public boolean isSimpleScheduling() {
        return sessionType == SessionType.COURSE || sessionType == SessionType.TUTORIAL;
    }
}
