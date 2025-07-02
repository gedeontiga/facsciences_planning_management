package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Branch;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;

import lombok.NonNull;

public record LevelDTO(
        String id,
        String code,
        String name,
        Long totalNumberOfStudents,
        String branchId) {
    public static LevelDTO fromLevel(@NonNull Level level) {
        return new LevelDTO(level.getId(),
                level.getCode(),
                level.getName(),
                level.getTotalNumberOfStudents(),
                Optional.ofNullable(level.getBranch()).map(Branch::getId).orElse(null));
    }
}
