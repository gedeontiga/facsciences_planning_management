package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.AcademicYear;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.validators.interfaces.ValidAcademicYear;

import io.swagger.v3.oas.annotations.media.Schema;

public record AcademicYearDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String id,
        @ValidAcademicYear String label) {

    public static AcademicYearDTO fromAcademicYear(AcademicYear academicYear) {
        return new AcademicYearDTO(academicYear.getId(), academicYear.getLabel());
    }
}
