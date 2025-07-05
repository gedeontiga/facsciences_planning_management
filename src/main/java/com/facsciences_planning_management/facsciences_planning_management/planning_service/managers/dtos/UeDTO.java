package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;

public record UeDTO(
        String id,
        String name,
        String code,
        Integer credits,
        String category,
        Integer hourlyCharge,
        String levelId,
        String levelCode,
        Long totalNumberOfStudents,
        Boolean assigned,
        Semester semester) {
    public static UeDTO fromUe(Ue ue) {
        return new UeDTO(
                ue.getId(), ue.getName(), ue.getCode(),
                ue.getCredits(), ue.getCategory(),
                ue.getHourlyCharge(), ue.getLevel().getId(),
                Optional.ofNullable(ue.getLevel()).map(l -> l.getCode()).orElse(null),
                Optional.ofNullable(ue.getLevel()).map(l -> l.getHeadCount()).orElse(null),
                ue.getAssigned(), ue.getSemester());
    }
}