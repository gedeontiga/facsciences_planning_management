package com.facsciencesuy1.planning_management.academic_service.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciencesuy1.planning_management.dtos.UeDTO;

public interface UeService {
    UeDTO createUe(UeDTO request);

    UeDTO getUeById(String id);

    Page<UeDTO> getAllUes(Pageable page);

    Page<UeDTO> getAllUnassignedUes(Pageable page);

    Page<UeDTO> getUesByLevel(String levelId, Pageable page);

    Page<UeDTO> getUnassignedUesByLevel(String levelId, Pageable page);

    List<UeDTO> getUesByCategoryAndLevel(String category, String levelId);

    List<UeDTO> getUesByCreditsAndLevel(Integer credits, String levelId);

    UeDTO updateUe(String id, UeDTO request);

    void deleteUe(String id);
}
