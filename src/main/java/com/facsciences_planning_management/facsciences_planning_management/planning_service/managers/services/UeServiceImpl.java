package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeUpdateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.UeService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.repositories.UeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UeServiceImpl implements UeService {
    private final UeRepository ueRepository;
    private final LevelRepository levelRepository;

    @Override
    public UeDTO createUe(UeCreateRequest request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + request.levelId()));

        Duration duration = Duration.parse(request.duration());

        Ue ue = Ue.builder()
                .name(request.name())
                .code(request.code())
                .credits(request.credits())
                .duration(duration)
                .hourlyCharge(request.hourlyCharge())
                .level(level)
                .build();

        return ueRepository.save(ue).toDTO();
    }

    @Override
    public UeDTO getUeById(String id) {
        return ueRepository.findById(id)
                .map(Ue::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("UE not found with id: " + id));
    }

    @Override
    public Ue getUeEntityById(String id) {
        return ueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UE not found with id: " + id));
    }

    @Override
    public List<UeDTO> getAllUes() {
        return ueRepository.findAll().stream()
                .map(Ue::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UeDTO> getUesByLevel(String levelId) {
        return ueRepository.findByLevelId(levelId).stream()
                .map(Ue::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ue> getUesByLevelId(String levelId) {
        return ueRepository.findByLevelId(levelId);
    }

    @Override
    public UeDTO updateUe(String id, UeUpdateRequest request) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UE not found with id: " + id));

        if (request.name() != null) {
            ue.setName(request.name());
        }

        if (request.code() != null) {
            ue.setCode(request.code());
        }

        if (request.credits() != null) {
            ue.setCredits(request.credits());
        }

        if (request.duration() != null) {
            ue.setDuration(Duration.parse(request.duration()));
        }

        if (request.hourlyCharge() != null) {
            ue.setHourlyCharge(request.hourlyCharge());
        }

        if (request.levelId() != null) {
            Level level = levelRepository.findById(request.levelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + request.levelId()));
            ue.setLevel(level);
        }

        return ueRepository.save(ue).toDTO();
    }

    @Override
    public void deleteUe(String id) {
        ueRepository.deleteById(id);
    }
}