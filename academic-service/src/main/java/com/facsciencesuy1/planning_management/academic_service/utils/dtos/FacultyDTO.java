package com.facsciencesuy1.planning_management.academic_service.utils.dtos;

import java.util.List;

import com.facsciencesuy1.planning_management.dtos.RoomDTO;
import com.facsciencesuy1.planning_management.entities.Faculty;

public record FacultyDTO(
        String id,
        String name,
        String code,
        List<BranchDTO> branches,
        List<RoomDTO> rooms) {
    public static FacultyDTO fromFaculty(Faculty faculty) {
        return new FacultyDTO(
                faculty.getId(),
                faculty.getName(),
                faculty.getCode(),
                faculty.getBranches().stream().map(BranchDTO::fromBranch).toList(),
                faculty.getRooms().stream().map(RoomDTO::fromRoom).toList());
    }
}
