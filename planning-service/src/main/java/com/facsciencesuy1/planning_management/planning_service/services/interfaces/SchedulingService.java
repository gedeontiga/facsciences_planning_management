package com.facsciencesuy1.planning_management.planning_service.services.interfaces;

import java.util.List;

import com.facsciencesuy1.planning_management.dtos.SchedulingDTO;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.SchedulingRequest;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.TimeSlotDTO;

public interface SchedulingService<T extends SchedulingDTO, K extends SchedulingRequest> {
    T createScheduling(K request);

    T getScheduling(String id);

    void deleteScheduling(String id);

    T updateScheduling(String id,
            K request);

    List<TimeSlotDTO> getTimeSlots();
}