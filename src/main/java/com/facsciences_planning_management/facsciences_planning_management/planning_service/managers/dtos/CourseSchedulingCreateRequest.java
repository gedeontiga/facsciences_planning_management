package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

import lombok.Builder;

public record CourseSchedulingCreateRequest(
        String roomId,
        String ueId,
        String timetableId,
        LocalTime startTime,
        LocalTime endTime,
        SessionType sessionType,
        String teacherId,
        DayOfWeek day) {
    @Builder
    public CourseSchedulingCreateRequest(String roomId, String ueId, String timetableId, LocalTime startTime,
            LocalTime endTime, SessionType sessionType, String teacherId, DayOfWeek day) {
        this.roomId = roomId;
        this.ueId = ueId;
        this.timetableId = timetableId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionType = sessionType;
        this.teacherId = teacherId;
        this.day = day;
    }
}