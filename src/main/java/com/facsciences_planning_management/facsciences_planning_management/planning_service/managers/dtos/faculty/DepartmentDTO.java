package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

public record DepartmentDTO(
        String id,
        @NotNull String name,
        @NotNull String code,
        @NotNull String branchId,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String branchName,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String branchCode) {
    public static DepartmentDTO fromDepartment(Department department) {
        return new DepartmentDTO(
                department.getId(),
                department.getName(),
                department.getCode(),
                Optional.ofNullable(department.getBranch()).map(b -> b.getId()).orElse(null),
                Optional.ofNullable(department.getBranch()).map(b -> b.getName()).orElse(null),
                Optional.ofNullable(department.getBranch()).map(b -> b.getCode()).orElse(null));
    }
}
