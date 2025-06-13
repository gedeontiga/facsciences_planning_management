package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

@Data
@Builder
public class ManualExamSchedulingRequestDTO {
    private String roomId;
    private String courseId;
    private String timetableId;
    private String proctorId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime sessionDate;
    private SessionType sessionType;
}
