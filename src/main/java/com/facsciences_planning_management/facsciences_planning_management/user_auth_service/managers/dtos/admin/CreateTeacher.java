package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.admin;

import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;

import jakarta.validation.constraints.Email;

public record CreateTeacher(
        String firstName,
        String lastName,
        @Email String email,
        RoleType role,
        String address,
        String phoneNumber,
        String departmentId) {
}
