package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import lombok.Builder;

@Builder
public record UeCreateRequest(
        String name,
        String code,
        Long credits,
        String duration,
        Integer hourlyCharge,
        String levelId) {
}