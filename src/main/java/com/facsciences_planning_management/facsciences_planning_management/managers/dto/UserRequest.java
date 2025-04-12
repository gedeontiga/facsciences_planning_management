package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for user registration")
public record UserRequest(
        @Schema(description = "User's email address", example = "student@facsciences-uy1.cm", required = true) String email,

        @Schema(description = "User's first name", example = "John", required = true) String firstName,

        @Schema(description = "User's last name", example = "Doe", required = true) String lastName,

        @Schema(description = "User's address", example = "123 Campus Road") String address,

        @Schema(description = "User's phone number", example = "+237123456789") String phoneNumber,

        @Schema(description = "User's password", example = "SecurePass123!", required = true) String password) {
}