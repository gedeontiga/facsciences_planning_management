package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.entities.Teacher;

public record TeacherDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        String departmentId,
        String departmentName,
        String departmentCode,
        String address,
        String phoneNumber) {
    public static TeacherDTO from(Teacher user) {
        return new TeacherDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDepartment().getId(),
                user.getDepartment().getName(),
                user.getDepartment().getCode(),
                user.getAddress(),
                user.getPhoneNumber());
    }
}