package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.AcademicYear;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record AcademicYearDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String id,
        @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in the format YYYY-YYYY") String label) {

    public static AcademicYearDTO fromAcademicYear(AcademicYear academicYear) {
        return new AcademicYearDTO(academicYear.getId(), academicYear.getLabel());
    }
}
