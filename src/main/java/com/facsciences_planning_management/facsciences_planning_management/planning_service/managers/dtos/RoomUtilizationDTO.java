package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

public record RoomUtilizationDTO(
        String roomId,
        String roomName,
        double weeklyHours,
        double usagePercentage,
        Long capacity) {
}