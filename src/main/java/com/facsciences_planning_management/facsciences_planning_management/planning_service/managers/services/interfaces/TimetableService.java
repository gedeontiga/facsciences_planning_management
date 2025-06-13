package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableUpdateRequest;

public interface TimetableService {
    TimetableDTO createTimetable(TimetableCreateRequest request);

    TimetableDTO getTimetableById(String id);

    Timetable getTimetableEntityById(String id);

    List<TimetableDTO> getAllTimetables();

    // PlanningDTO getCurrentPlanning();

    TimetableDTO updateTimetable(String id, TimetableUpdateRequest request);

    void deleteTimetable(String id);

    TimetableDTO getDetailedTimetableById(String id);
}
