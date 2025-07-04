package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;

public sealed interface SchedulingDTO permits CourseSchedulingDTO, ExamSchedulingDTO {
    String id();

    String roomId();

    String roomCode();

    String ueId();

    String ueCode();

    String userId();

    String timetableId();

    String timeSlotLabel();

    String startTime();

    String endTime();

    Long headCount();

    HeadCountLabel headCountLabel();
}