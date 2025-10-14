package com.facsciencesuy1.planning_management.academic_service.utils.dtos;

public record CourseRequest(
		String teacherId,
		String ueId,
		Long duration,
		String departmentId) {
}
