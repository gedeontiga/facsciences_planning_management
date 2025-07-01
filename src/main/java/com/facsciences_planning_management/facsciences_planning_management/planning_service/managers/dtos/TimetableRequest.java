package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.AcademicYearFormat;

import jakarta.validation.Valid;

public record TimetableRequest(
        String levelId,
        @AcademicYearFormat String academicYear,
        String semester,
        @Valid SessionType sessionType) {
}
