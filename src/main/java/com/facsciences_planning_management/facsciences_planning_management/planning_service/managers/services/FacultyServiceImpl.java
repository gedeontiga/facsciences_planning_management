package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Branch;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Department;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Faculty;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.BranchRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.DepartmentRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.FacultyRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.LevelDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.FacultyService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final BranchRepository branchRepository;
    private final LevelRepository levelRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public FacultyDTO createFaculty(FacultyRequest request) {

        if (facultyRepository.existsByCode(request.code())) {
            throw new CustomBusinessException("Faculty with code " + request.code() + " already exists.");
        }
        Faculty faculty = Faculty.builder()
                .name(request.name())
                .code(request.code())
                .build();
        return FacultyDTO.fromFaculty(facultyRepository.save(faculty));
    }

    @Override
    public BranchDTO createBranch(BranchRequest request) {

        Faculty faculty = facultyRepository.findById(request.facultyId())
                .orElseThrow(() -> new CustomBusinessException("Faculty not found for this id"));
        if (branchRepository.existsByCode(request.code())) {
            throw new CustomBusinessException(
                    "Branch with code " + request.code() + " already exists in this faculty.");
        }
        Branch branch = Branch.builder()
                .name(request.name())
                .code(request.code())
                .faculty(faculty)
                .build();
        faculty.getBranches().add(branch);
        facultyRepository.save(faculty);
        return BranchDTO.fromBranch(branchRepository.save(branch));
    }

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {

        Branch branch = branchRepository.findById(departmentDTO.branchId())
                .orElseThrow(() -> new CustomBusinessException("Branch not found for this id"));
        if (departmentRepository.existsByCode(departmentDTO.code())) {
            throw new CustomBusinessException(
                    "Department with code " + departmentDTO.code() + " already exists in this branch.");
        }
        Department department = departmentRepository.save(Department.builder()
                .name(departmentDTO.name())
                .code(departmentDTO.code())
                .branch(branch)
                .build());
        branch.setDepartment(department);
        branchRepository.save(branch);
        return DepartmentDTO.fromDepartment(department);
    }

    @Override
    public LevelDTO createLevel(LevelDTO levelDTO) {

        Branch branch = branchRepository.findById(levelDTO.branchId())
                .orElseThrow(() -> new CustomBusinessException("Branch not found for this id"));
        if (levelRepository.existsByCode(levelDTO.code())) {
            throw new CustomBusinessException("Level with code " + levelDTO.code() + " already exists in this branch.");
        }
        Level level = levelRepository.save(Level.builder()
                .name(levelDTO.name())
                .code(levelDTO.code())
                .branch(branch)
                .build());
        branch.getLevels().add(level);
        branchRepository.save(branch);
        return LevelDTO.fromLevel(level);
    }

    @Override
    public LevelDTO updateLevel(String levelId, Long headCount) {

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new CustomBusinessException("Level not found with id: " + levelId));
        level.setHeadCount(headCount);
        return LevelDTO.fromLevel(levelRepository.save(level));
    }

    @Override
    public List<LevelDTO> getLevelsByBranch(String branchId) {

        if (!branchRepository.existsById(branchId)) {
            throw new CustomBusinessException("Branch not found with id: " + branchId);
        }
        return levelRepository.findByBranchId(branchId)
                .stream()
                .map(LevelDTO::fromLevel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchDTO> getAllBranchesByFaculty(String facultyId) {

        if (!facultyRepository.existsById(facultyId)) {
            throw new CustomBusinessException("Faculty not found with id: " + facultyId);
        }
        return branchRepository.findByFacultyId(facultyId)
                .stream()
                .map(BranchDTO::fromBranch)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDTO> getAllDepartmentsByFaculty(String facultyId) {

        if (!facultyRepository.existsById(facultyId)) {
            throw new CustomBusinessException("Faculty not found with id: " + facultyId);
        }
        return departmentRepository.findByBranchFacultyId(facultyId)
                .stream()
                .map(DepartmentDTO::fromDepartment)
                .collect(Collectors.toList());
    }

    @Override
    public List<FacultyDTO> getAllFaculties() {

        return facultyRepository.findAll()
                .stream()
                .map(FacultyDTO::fromFaculty)
                .collect(Collectors.toList());
    }
}
