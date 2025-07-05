package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@NotNull
@NotBlank
public record BranchRequest(
		String name,
		String code,
		String facultyId) {
}
