package com.facsciences_planning_management.facsciences_planning_management.dto;

public record ErrorResponse(
        String error,
        String message,
        int status,
        String timestamp) {
}