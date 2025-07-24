package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import jakarta.validation.constraints.Size;

public record PasswordResetRequest(String token, @Size(min = 8) String newPassword) {
}