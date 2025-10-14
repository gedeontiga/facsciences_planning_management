package com.facsciencesuy1.planning_management.academic_service.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciencesuy1.planning_management.academic_service.utils.dtos.BranchDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.BranchRequest;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.DepartmentCreation;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.DepartmentDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.FacultyDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.FacultyRequest;
import com.facsciencesuy1.planning_management.dtos.LevelDTO;
import com.facsciencesuy1.planning_management.dtos.TeacherDTO;

public interface FacultyService {

    FacultyDTO createFaculty(FacultyRequest faculty);

    BranchDTO createBranch(BranchRequest request);

    LevelDTO createLevel(LevelDTO levelDTO);

    LevelDTO updateLevel(String levelId, Long headCount);

    DepartmentDTO createDepartment(DepartmentCreation department);

    List<LevelDTO> getLevelsByBranch(String branchId);

    List<BranchDTO> getAllBranchesByFaculty(String facultyId);

    List<DepartmentDTO> getAllDepartmentsByFaculty(String facultyId);

    List<FacultyDTO> getAllFaculties();

    Page<TeacherDTO> getTeachersByDepartment(String departmentId, Pageable page);
}
