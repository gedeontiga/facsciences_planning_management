package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.types.HeadCountLabel;

public interface SchedulingRequest {

    String roomId();

    String ueId();

    String userId();

    String timetableId();

    String timeSlotLabel();

    Long headCount();

    HeadCountLabel headCountLabel();
}
