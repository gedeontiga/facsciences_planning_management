package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.time.Year;

public record TimetableUpdateRequest(
                Year academicYear,
                String semester) {
}