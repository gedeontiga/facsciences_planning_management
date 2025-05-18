package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.Year;

import lombok.Builder;

@Builder
public record PlanningUpdateRequest(
        Year academicYear,
        String semester) {
}