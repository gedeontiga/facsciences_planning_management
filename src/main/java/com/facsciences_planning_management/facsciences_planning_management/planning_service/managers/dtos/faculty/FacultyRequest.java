package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import jakarta.validation.constraints.NotNull;

@NotNull
public record FacultyRequest(
		String name,
		String code) {
}
