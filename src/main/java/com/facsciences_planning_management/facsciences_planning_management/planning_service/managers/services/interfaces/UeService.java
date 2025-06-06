package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeUpdateRequest;

public interface UeService {
    UeDTO createUe(UeCreateRequest request);

    UeDTO getUeById(String id);

    Ue getUeEntityById(String id);

    List<UeDTO> getAllUes();

    List<UeDTO> getUesByLevel(String levelId);

    List<Ue> getUesByLevelId(String levelId);

    UeDTO updateUe(String id, UeUpdateRequest request);

    void deleteUe(String id);
}
