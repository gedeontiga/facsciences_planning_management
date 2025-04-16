package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

public record PasswordResetRequest(String token, String newPassword) {
}