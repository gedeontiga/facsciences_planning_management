package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.dto.TeacherDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.LevelDTO;

public interface FacultyService {

    FacultyDTO createFaculty(FacultyRequest faculty);

    BranchDTO createBranch(BranchRequest request);

    LevelDTO createLevel(LevelDTO levelDTO);

    LevelDTO updateLevel(String levelId, Long headCount);

    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    List<LevelDTO> getLevelsByBranch(String branchId);

    List<BranchDTO> getAllBranchesByFaculty(String facultyId);

    List<DepartmentDTO> getAllDepartmentsByFaculty(String facultyId);

    List<FacultyDTO> getAllFaculties();

    Page<TeacherDTO> getTeachersByDepartment(String departmentId, Pageable page);
}
