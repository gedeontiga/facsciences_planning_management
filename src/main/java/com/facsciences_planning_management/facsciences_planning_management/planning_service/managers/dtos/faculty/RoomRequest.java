package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;

public record RoomRequest(
		String code,
		RoomType type,
		Long capacity,
		String building,
		String facultyId) {
}