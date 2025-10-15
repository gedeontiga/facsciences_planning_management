package com.facsciencesuy1.planning_management.api_gateway.utils.dtos;

public record LoginResponse(String bearer, String role, String expiresAt, String expiresIn) {
}
