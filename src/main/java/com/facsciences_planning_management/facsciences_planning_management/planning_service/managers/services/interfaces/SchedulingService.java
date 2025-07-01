package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;

public interface SchedulingService<T extends SchedulingDTO> {
    T createScheduling(T request);

    T getScheduling(String id);

    void deleteScheduling(String id);

    T updateScheduling(String id,
            T request);

    List<TimeSlotDTO> getTimeSlots();
}