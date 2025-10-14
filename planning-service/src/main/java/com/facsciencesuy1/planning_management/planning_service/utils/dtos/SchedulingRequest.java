package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import com.facsciencesuy1.planning_management.dtos.types.HeadCountLabel;

public interface SchedulingRequest {

    String roomId();

    String ueId();

    String userId();

    String timetableId();

    String timeSlotLabel();

    Long headCount();

    HeadCountLabel headCountLabel();
}
