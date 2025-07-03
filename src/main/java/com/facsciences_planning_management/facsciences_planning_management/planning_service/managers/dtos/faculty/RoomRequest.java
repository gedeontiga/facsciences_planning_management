package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;

import jakarta.validation.constraints.NotNull;

public record RoomRequest(
		@NotNull String code,
		@NotNull RoomType type,
		Long capacity,
		String building,
		@NotNull String facultyId) {
}