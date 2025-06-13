package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

public record UeUpdateRequest(
        String name,
        String code,
        Integer credits,
        String duration,
        String category,
        Integer hourlyCharge,
        String levelId) {
}