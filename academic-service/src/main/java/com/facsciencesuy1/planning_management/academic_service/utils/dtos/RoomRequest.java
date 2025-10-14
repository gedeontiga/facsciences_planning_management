package com.facsciencesuy1.planning_management.academic_service.utils.dtos;

import com.facsciencesuy1.planning_management.entities.types.RoomType;

public record RoomRequest(
		String code,
		RoomType type,
		Long capacity,
		String building,
		String facultyId) {
}