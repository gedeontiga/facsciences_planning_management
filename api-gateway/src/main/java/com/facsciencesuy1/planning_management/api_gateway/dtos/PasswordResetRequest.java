package com.facsciencesuy1.planning_management.api_gateway.dtos;

import jakarta.validation.constraints.Size;

public record PasswordResetRequest(String token, @Size(min = 8) String newPassword) {
}