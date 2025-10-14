package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import com.facsciencesuy1.planning_management.entities.types.Semester;
import com.facsciencesuy1.planning_management.entities.types.SessionType;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidAcademicYear;

public record TimetableRequest(
		String levelId,
		@ValidAcademicYear String academicYear,
		Semester semester,
		SessionType sessionType) {
}
