package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Branch;

public record BranchDTO(
        String id,
        String name,
        String code,
        List<LevelDTO> levels) {
    public static BranchDTO fromBranch(Branch branch) {
        return new BranchDTO(
                branch.getId(),
                branch.getName(),
                branch.getCode(),
                branch.getLevels().stream().map(LevelDTO::fromLevel).toList());
    }
}
