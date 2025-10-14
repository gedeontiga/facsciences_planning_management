package com.facsciencesuy1.planning_management.planning_service.utils.dtos;

import com.facsciencesuy1.planning_management.entities.AcademicYear;
import com.facsciencesuy1.planning_management.planning_service.utils.validators.interfaces.ValidAcademicYear;

public record AcademicYearDTO(
        String id,
        @ValidAcademicYear String label) {

    public static AcademicYearDTO fromAcademicYear(AcademicYear academicYear) {
        return new AcademicYearDTO(academicYear.getId(), academicYear.getLabel());
    }
}
