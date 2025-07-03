package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Nonnull
public record TimetableRequest(
                String levelId,
                @Valid @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in the format YYYY-YYYY") String academicYear,
                @Valid Semester semester,
                @Valid SessionType sessionType) {
}
