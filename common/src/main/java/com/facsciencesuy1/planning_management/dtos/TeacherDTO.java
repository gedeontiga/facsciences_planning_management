package com.facsciencesuy1.planning_management.dtos;

import com.facsciencesuy1.planning_management.entities.Teacher;

public record TeacherDTO(String id, String firstName, String lastName, String email, String departmentId,
        String departmentName, String departmentCode, String address, String phoneNumber) {
    public static TeacherDTO fromTeacher(Teacher user, String departmentName, String departmentCode) {
        return new TeacherDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getDepartmentId(), departmentName, departmentCode, user.getAddress(), user.getPhoneNumber());
    }
}