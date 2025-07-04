package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;

public interface SchedulingService<T extends SchedulingDTO, K extends SchedulingRequest> {
    T createScheduling(K request);

    T getScheduling(String id);

    void deleteScheduling(String id);

    T updateScheduling(String id,
            K request);

    List<TimeSlotDTO> getTimeSlots();
}