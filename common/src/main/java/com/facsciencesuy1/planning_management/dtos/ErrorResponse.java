package com.facsciencesuy1.planning_management.dtos;

public record ErrorResponse(
                String error,
                String message,
                int status,
                String timestamp) {
}