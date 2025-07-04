package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidAcademicYear;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@NotNull
public record TimetableRequest(
        String levelId,
        @Valid @ValidAcademicYear String academicYear,
        @Valid Semester semester,
        @Valid SessionType sessionType) {
}
