package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

public record UserUpdate(
        String firstName,
        String lastName,
        String address,
        String phoneNumber) {
}
