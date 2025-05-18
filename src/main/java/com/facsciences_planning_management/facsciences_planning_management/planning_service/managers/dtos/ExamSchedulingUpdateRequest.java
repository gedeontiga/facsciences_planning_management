package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.SessionType;

import lombok.Builder;

@Builder
public record ExamSchedulingUpdateRequest(
        String roomId,
        String ueId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        String proctorId,
        LocalDateTime sessionDate) {
}