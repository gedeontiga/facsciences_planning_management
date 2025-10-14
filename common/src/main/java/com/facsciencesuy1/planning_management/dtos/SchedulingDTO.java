package com.facsciencesuy1.planning_management.dtos;

import com.facsciencesuy1.planning_management.dtos.types.HeadCountLabel;

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