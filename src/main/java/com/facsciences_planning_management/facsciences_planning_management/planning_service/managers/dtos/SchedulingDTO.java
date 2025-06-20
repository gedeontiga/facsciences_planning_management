package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

public interface SchedulingDTO {
    String id();

    String roomId();

    String ueId();

    String timetableId();

    LocalTime startTime();

    LocalTime endTime();

    SessionType sessionType();
}