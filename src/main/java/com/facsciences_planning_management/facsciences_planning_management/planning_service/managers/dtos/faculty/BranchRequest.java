package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import jakarta.annotation.Nonnull;

@Nonnull
public record BranchRequest(
		String name,
		String code,
		String facultyId) {
}
