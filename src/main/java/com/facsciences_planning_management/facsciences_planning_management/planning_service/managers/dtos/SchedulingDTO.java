package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.SessionType;

public interface SchedulingDTO {
    String id();

    String roomId();

    String ueId();

    String planningId();

    LocalTime startTime();

    LocalTime endTime();

    SessionType sessionType();
}