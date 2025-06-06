package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Planning;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningUpdateRequest;

public interface PlanningService {
    PlanningDTO createPlanning(PlanningCreateRequest request);

    PlanningDTO getPlanningById(String id);

    Planning getPlanningEntityById(String id);

    List<PlanningDTO> getAllPlannings();

    // PlanningDTO getCurrentPlanning();

    PlanningDTO updatePlanning(String id, PlanningUpdateRequest request);

    void deletePlanning(String id);

    PlanningDTO getDetailedPlanningById(String id);
}
