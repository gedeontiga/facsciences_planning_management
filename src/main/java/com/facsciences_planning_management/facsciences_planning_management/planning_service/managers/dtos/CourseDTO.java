package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;

public record CourseDTO(
        String id,
        String teacherId,
        String teacherFirstName,
        String teacherLastName,
        String ueId,
        String ueCode,
        Long duration) {
    public static CourseDTO fromCourse(Course entity) {
        return new CourseDTO(
                entity.getId(),
                entity.getTeacher().getId(),
                entity.getTeacher().getFirstName(),
                entity.getTeacher().getLastName(),
                entity.getUe().getId(),
                entity.getUe().getCode(),
                entity.getDuration().toHours());
    }
}
