package com.facsciencesuy1.planning_management.api_gateway.dtos;

import jakarta.validation.constraints.Email;

public record LoginRequest(@Email String email, String password) {
}
