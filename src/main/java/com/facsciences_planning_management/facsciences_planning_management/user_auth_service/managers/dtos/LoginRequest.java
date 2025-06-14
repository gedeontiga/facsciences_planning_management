package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import jakarta.validation.constraints.Email;

public record LoginRequest(@Email String email, String password) {
}
