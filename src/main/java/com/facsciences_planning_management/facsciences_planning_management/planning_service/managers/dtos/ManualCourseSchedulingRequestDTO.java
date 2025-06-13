package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManualCourseSchedulingRequestDTO {
    private String roomId;
    private String courseId;
    private String timetableId;
    private String teacherId;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek day;
    private SessionType sessionType;
}
