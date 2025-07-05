package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomRequest(
		@NotBlank @NotNull String code,
		@NotBlank @NotNull RoomType type,
		@NotBlank @NotNull Long capacity,
		@NotBlank @NotNull String building,
		@NotBlank @NotNull String facultyId) {
}