package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Planning;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.SimpleScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ExamSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.PlanningRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.SimpleSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.PlanningUpdateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceInUseException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.PlanningService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanningServiceImpl implements PlanningService {
    private final PlanningRepository planningRepository;
    private final SimpleSchedulingRepository simpleSchedulingRepository;
    private final ExamSchedulingRepository examSchedulingRepository;

    @Override
    public PlanningDTO createPlanning(PlanningCreateRequest request) {
        Planning planning = Planning.builder()
                .academicYear(request.academicYear())
                .semester(request.semester())
                .createdAt(LocalDateTime.now())
                .build();

        return planningRepository.save(planning).toDTO();
    }

    @Override
    public PlanningDTO getPlanningById(String id) {
        return planningRepository.findById(id)
                .map(Planning::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id: " + id));
    }

    @Override
    public Planning getPlanningEntityById(String id) {
        return planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id: " + id));
    }

    @Override
    public List<PlanningDTO> getAllPlannings() {
        return planningRepository.findAll().stream()
                .map(Planning::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlanningDTO updatePlanning(String id, PlanningUpdateRequest request) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id: " + id));

        if (request.academicYear() != null) {
            planning.setAcademicYear(request.academicYear());
        }

        if (request.semester() != null) {
            planning.setSemester(request.semester());
        }

        return planningRepository.save(planning).toDTO();
    }

    @Override
    public void deletePlanning(String id) {
        // First check if there are any schedules referring to this planning
        if (!simpleSchedulingRepository.findByPlanningId(id).isEmpty() ||
                !examSchedulingRepository.findByPlanningId(id).isEmpty()) {
            throw new ResourceInUseException("Cannot delete planning with associated schedules");
        }

        planningRepository.deleteById(id);
    }

    @Override
    public PlanningDTO getDetailedPlanningById(String id) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id: " + id));

        // Get all schedules for this planning
        List<SimpleScheduling> simpleSchedules = simpleSchedulingRepository.findByPlanningId(id);
        List<ExamScheduling> examSchedules = examSchedulingRepository.findByPlanningId(id);

        // Convert to DTOs and merge into a single set
        Set<SchedulingDTO> allSchedules = new HashSet<>();
        allSchedules.addAll(simpleSchedules.stream().map(SimpleScheduling::toDTO).collect(Collectors.toSet()));
        allSchedules.addAll(examSchedules.stream().map(ExamScheduling::toDTO).collect(Collectors.toSet()));

        return PlanningDTO.fromPlanning(planning);
    }
}