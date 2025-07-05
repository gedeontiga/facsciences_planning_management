package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;

public record CourseDTO(
        String id,
        String teacherId,
        String teacherFirstName,
        String teacherLastName,
        String ueId,
        String ueCode,
        Long duration,
        String departmentId) {
    public static CourseDTO fromCourse(Course entity) {
        return new CourseDTO(
                entity.getId(),
                Optional.ofNullable(entity.getTeacher()).map(u -> u.getId()).orElse(null),
                Optional.ofNullable(entity.getTeacher()).map(u -> u.getFirstName()).orElse(null),
                Optional.ofNullable(entity.getTeacher()).map(u -> u.getLastName()).orElse(null),
                Optional.ofNullable(entity.getUe()).map(ue -> ue.getId()).orElse(null),
                Optional.ofNullable(entity.getUe()).map(ue -> ue.getCode()).orElse(null),
                Optional.ofNullable(entity.getDuration()).map(d -> d.toHours()).orElse(null),
                Optional.ofNullable(entity.getTeacher()).map(d -> d.getDepartmentId()).orElse(null));
    }
}
