package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.AcademicYear;

public record AcademicYearDTO(
        String id,
        String label) {

    public static AcademicYearDTO fromAcademicYear(AcademicYear academicYear) {
        return new AcademicYearDTO(academicYear.getId(), academicYear.getLabel());
    }
}
