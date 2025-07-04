package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidAcademicYear;

import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;

@Nonnull
public record TimetableRequest(
        String levelId,
        @Valid @ValidAcademicYear String academicYear,
        @Valid Semester semester,
        @Valid SessionType sessionType) {
}
