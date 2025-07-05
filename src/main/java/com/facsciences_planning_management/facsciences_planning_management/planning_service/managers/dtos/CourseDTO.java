package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

public record CourseDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String id,
        @NotNull String teacherId,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String teacherFirstName,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String teacherLastName,
        @NotNull String ueId,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String ueCode,
        @NotNull Long duration,
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
                null);
    }

    public CourseDTO withDepartment(String departmentId) {
        return new CourseDTO(
                this.id,
                this.teacherId,
                this.teacherFirstName,
                this.teacherLastName,
                this.ueId,
                this.ueCode,
                this.duration,
                departmentId);
    }
}
