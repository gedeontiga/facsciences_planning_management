package com.facsciencesuy1.planning_management.user_service.utils.dtos.admin;

import com.facsciencesuy1.planning_management.entities.types.RoleType;

import jakarta.validation.constraints.Email;

public record CreateTeacher(String firstName, String lastName, @Email String email, RoleType role, String address,
                String phoneNumber, String departmentId) {
}
