package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;
import java.util.stream.Collectors;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Planning;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Scheduling;

import lombok.Builder;
import lombok.Singular;

@Builder
public record PlanningDTO(
        String id,
        @Singular Set<SchedulingDTO> schedules,
        Year academicYear,
        String semester,
        LocalDateTime createdAt) {
    public static PlanningDTO fromPlanning(Planning entity) {
        return new PlanningDTO(entity.getId(),
                entity.getSchedules().stream()
                        .map(Scheduling::toDTO)
                        .collect(Collectors.toSet()),
                entity.getAcademicYear(),
                entity.getSemester(),
                entity.getCreatedAt());
    }
}