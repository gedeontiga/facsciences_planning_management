package com.facsciencesuy1.planning_management.academic_service.utils.dtos;

import java.util.Optional;

import com.facsciencesuy1.planning_management.entities.Department;

public record DepartmentDTO(
        String id,
        String name,
        String code,
        String branchId,
        String branchName,
        String branchCode) {
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
