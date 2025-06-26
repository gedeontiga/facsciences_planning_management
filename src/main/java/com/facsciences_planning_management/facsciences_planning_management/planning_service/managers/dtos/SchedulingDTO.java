package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalTime;

public sealed interface SchedulingDTO permits CourseSchedulingDTO, ExamSchedulingDTO {
    String id();

    String roomId();

    String roomCode();

    String ueId();

    String ueCode();

    String timetableId();

    String timeSlotLabel();

    LocalTime startTime();

    LocalTime endTime();
}