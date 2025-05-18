package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import lombok.Builder;

@Builder
public record DashboardOverviewDTO(
        long availableRooms,
        long totalRooms,
        long teacherCount,
        long studentCount) {
}