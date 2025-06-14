package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;

public record UeDTO(
        String id,
        String name,
        String code,
        Integer credits,
        String category,
        Integer hourlyCharge,
        String levelId
// Optional<String> levelCode,
// Optional<Long> totalNumberOfStudents
) {
    public static UeDTO fromUe(Ue ue) {
        return new UeDTO(
                ue.getId(), ue.getName(), ue.getCode(),
                ue.getCredits(), ue.getCategory(),
                ue.getHourlyCharge(), ue.getLevel().getId());
        // Optional.of(ue.getLevel().getCode()),
        // Optional.of(ue.getLevel().getTotalNumberOfStudents()));
    }
}