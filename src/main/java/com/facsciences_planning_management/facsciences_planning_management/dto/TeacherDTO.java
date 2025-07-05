package com.facsciences_planning_management.facsciences_planning_management.dto;

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
    public static TeacherDTO fromTeacher(Teacher user, String departmentName, String departmentCode) {
        return new TeacherDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDepartmentId(),
                departmentName,
                departmentCode,
                user.getAddress(),
                user.getPhoneNumber());
    }
}