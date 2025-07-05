package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

public record CourseRequest(
		String teacherId,
		String ueId,
		Long duration,
		String departmentId) {
}
