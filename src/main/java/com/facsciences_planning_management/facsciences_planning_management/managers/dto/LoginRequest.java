package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

// import io.swagger.v3.oas.annotations.media.Schema;

// @Schema(description = "Request object for user login")
public record LoginRequest(
                // @Schema(description = "User's email address", example =
                // "student@facsciences-uy1.cm", required = true)
                String email,

                // @Schema(description = "User's password", example = "SecurePass123!", required
                // = true)
                String password) {
}
