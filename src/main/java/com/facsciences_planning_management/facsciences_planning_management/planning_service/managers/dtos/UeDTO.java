package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UeDTO(
        String id,
        @NotBlank @NotNull String name,
        @NotBlank @NotNull String code,
        @NotBlank @NotNull Integer credits,
        @NotBlank @NotNull String category,
        @NotBlank @NotNull Integer hourlyCharge,
        @NotBlank @NotNull String levelId,
        String levelCode,
        @NotBlank @NotNull Long totalNumberOfStudents,
        Boolean assigned,
        @NotBlank @NotNull Semester semester) {
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