package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

public record TimetableErrorResponse(
        int status,
        String error,
        String message,
        long timestamp) {
}
