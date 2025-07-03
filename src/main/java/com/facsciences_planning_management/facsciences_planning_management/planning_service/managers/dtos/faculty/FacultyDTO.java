package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Faculty;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;

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
