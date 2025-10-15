package com.facsciencesuy1.planning_management.api_gateway.utils.dtos;

import jakarta.validation.constraints.Size;

public record PasswordResetRequest(String token, @Size(min = 8) String newPassword) {
}