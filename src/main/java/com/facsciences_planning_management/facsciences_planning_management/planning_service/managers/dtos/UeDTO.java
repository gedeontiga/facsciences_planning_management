package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;

public record UeDTO(
        String id,
        String name,
        String code,
        Long credits,
        String duration,
        String category,
        Integer hourlyCharge,
        LevelDTO level) {
    public static UeDTO fromUe(Ue ue) {
        return new UeDTO(ue.getId(), ue.getName(), ue.getCode(), ue.getCredits(), ue.getDuration().toString(),
                ue.getCategory(), ue.getHourlyCharge(), LevelDTO.fromLevel(ue.getLevel()));
    }
}