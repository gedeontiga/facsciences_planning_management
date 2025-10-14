package com.facsciencesuy1.planning_management.dtos;

import java.util.Optional;

import com.facsciencesuy1.planning_management.entities.Branch;
import com.facsciencesuy1.planning_management.entities.Level;

public record LevelDTO(String id, String code, String name, Long headCount, String branchId) {
    public static LevelDTO fromLevel(Level level) {
        return new LevelDTO(level.getId(), level.getCode(), level.getName(), level.getHeadCount(),
                Optional.ofNullable(level.getBranch()).map(Branch::getId).orElse(null));
    }
}
