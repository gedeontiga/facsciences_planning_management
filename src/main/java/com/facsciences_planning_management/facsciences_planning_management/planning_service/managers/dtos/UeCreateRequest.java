package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

public record UeCreateRequest(
        String name,
        String code,
        Integer credits,
        String duration,
        Integer hourlyCharge,
        String category,
        String levelId) {
}