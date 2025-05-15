package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto;

public record UserUpdateRequest(
        String email,
        String role) {
}
