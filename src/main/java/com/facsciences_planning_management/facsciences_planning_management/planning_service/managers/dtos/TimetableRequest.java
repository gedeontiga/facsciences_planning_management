package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidAcademicYear;

public record TimetableRequest(
		String levelId,
		@ValidAcademicYear String academicYear,
		Semester semester,
		SessionType sessionType) {
}
