package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Branch;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Level;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record LevelDTO(
        String id,
        String code,
        Long totalNumberOfStudents,
        String branchId) {
    public static LevelDTO fromLevel(@NonNull Level level) {
        return new LevelDTO(level.getId(),
                level.getCode(),
                level.getTotalNumberOfStudents(),
                Optional.ofNullable(level.getBranch()).map(Branch::getId).orElse(null));
    }
}
