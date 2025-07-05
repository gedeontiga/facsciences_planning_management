package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import jakarta.validation.constraints.NotNull;

public record AdminUserRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String email,
        @NotNull String role,
        @NotNull String address,
        @NotNull String phoneNumber,
        String departmentId) {

}
