package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;

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
                department.getBranch().getId(),
                department.getBranch().getName(),
                department.getBranch().getCode());
    }
}
