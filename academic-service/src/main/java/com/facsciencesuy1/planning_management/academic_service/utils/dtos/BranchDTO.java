package com.facsciencesuy1.planning_management.academic_service.utils.dtos;

import java.util.List;

import com.facsciencesuy1.planning_management.dtos.LevelDTO;
import com.facsciencesuy1.planning_management.entities.Branch;

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
