package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.LevelDTO;

public interface FacultyService {
    List<LevelDTO> getLevelsByBranch(String branchId);

    List<BranchDTO> getAllBranchesByFaculty(String facultyId);

    List<DepartmentDTO> getAllDepartmentsByFaculty(String facultyId);

    List<FacultyDTO> getAllFaculties();
}
