package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

// import io.swagger.v3.oas.annotations.media.Schema;

// @Schema(description = "Request object for password reset")
public record PasswordResetRequest(

        // @Schema(description = "Reset token", example = "uuid-token-here", required =
        // true)
        String token,

        // @Schema(description = "New password", example = "NewPass123!", required =
        // true)
        String newPassword) {
}