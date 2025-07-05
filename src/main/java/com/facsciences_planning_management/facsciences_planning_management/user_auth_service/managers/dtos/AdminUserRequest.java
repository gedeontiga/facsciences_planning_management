package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

public record AdminUserRequest(
        String firstName,
        String lastName,
        String email,
        String role,
        String address,
        String phoneNumber,
        String departmentId) {

}
