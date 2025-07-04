package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

public record LoginResponse(
		String bearer,
		String role,
		String expiresAt,
		String expiresIn) {
}
