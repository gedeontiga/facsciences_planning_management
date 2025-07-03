package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import com.facsciences_planning_management.facsciences_planning_management.components.annotations.SafeMapping;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;

import io.swagger.v3.oas.annotations.media.Schema;

@SafeMapping
public record DepartmentDTO(
        String id,
        String name,
        String code,
        String branchId,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String branchName,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String branchCode) {
    public static DepartmentDTO fromDepartment(Department department) {
        return new DepartmentDTO(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getBranch().getId(),
                department.getBranch().getName(),
                department.getBranch().getCode());
    }
}
