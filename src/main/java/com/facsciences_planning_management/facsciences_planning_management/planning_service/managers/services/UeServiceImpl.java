package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.UeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UeServiceImpl implements UeService {
    private final UeRepository ueRepository;
    private final LevelRepository levelRepository;

    @Override
    public UeDTO createUe(UeDTO request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomBusinessException("Level not found with id: " + request.levelId()));

        Ue ue = Ue.builder()
                .name(request.name())
                .code(request.code())
                .credits(request.credits())
                .category(request.category())
                .hourlyCharge(request.hourlyCharge())
                .level(level)
                .build();

        return ueRepository.save(ue).toDTO();
    }

    @Override
    public UeDTO getUeById(String id) {
        return ueRepository.findById(id)
                .map(Ue::toDTO)
                .orElseThrow(() -> new CustomBusinessException("UE not found with id: " + id));
    }

    @Override
    public UeDTO getUeByCode(String code) {
        return ueRepository.findByCode(code)
                .map(Ue::toDTO)
                .orElseThrow(() -> new CustomBusinessException("UE not found with code: " + code));
    }

    @Override
    public List<UeDTO> getUesByCategoryAndLevel(String category, String levelId) {
        return ueRepository.findByCategoryAndLevelId(category, levelId).stream()
                .map(Ue::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UeDTO> getUesByCreditsAndLevel(Integer credits, String levelId) {
        return ueRepository.findByCreditsAndLevelId(credits, levelId).stream()
                .map(Ue::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UeDTO> getAllUes(Pageable page) {
        return ueRepository.findAll(page)
                .map(Ue::toDTO);
    }

    @Override
    public Page<UeDTO> getAllUnassignedUes(Pageable page) {
        return ueRepository.findByAssignedFalse(page)
                .map(Ue::toDTO);
    }

    @Override
    public Page<UeDTO> getUesByLevel(String levelId, Pageable page) {
        return ueRepository.findByLevelId(levelId, page)
                .map(Ue::toDTO);
    }

    @Override
    public Page<UeDTO> getUnassignedUesByLevel(String levelId, Pageable page) {
        return ueRepository.findByLevelIdAndAssignedFalse(levelId, page)
                .map(Ue::toDTO);
    }

    @Override
    public UeDTO updateUe(String id, UeDTO request) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("UE not found with id: " + id));

        if (request.name() != null) {
            ue.setName(request.name());
        }

        if (request.code() != null) {
            ue.setCode(request.code());
        }

        if (request.credits() != null) {
            ue.setCredits(request.credits());
        }

        if (request.category() != null) {
            ue.setCategory(request.category());
        }

        if (request.hourlyCharge() != null) {
            ue.setHourlyCharge(request.hourlyCharge());
        }

        if (request.levelId() != null) {
            Level level = levelRepository.findById(request.levelId())
                    .orElseThrow(() -> new CustomBusinessException("Level not found with id: " + request.levelId()));
            ue.setLevel(level);
        }

        return ueRepository.save(ue).toDTO();
    }

    @Override
    public void deleteUe(String id) {
        ueRepository.deleteById(id);
    }
}