package com.facsciencesuy1.planning_management.academic_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.facsciencesuy1.planning_management.academic_service.repositories.BranchRepository;
import com.facsciencesuy1.planning_management.academic_service.repositories.DepartmentRepository;
import com.facsciencesuy1.planning_management.academic_service.repositories.FacultyRepository;
import com.facsciencesuy1.planning_management.academic_service.repositories.LevelRepository;
import com.facsciencesuy1.planning_management.academic_service.repositories.TeacherRepository;
import com.facsciencesuy1.planning_management.academic_service.services.interfaces.FacultyService;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.BranchDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.BranchRequest;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.DepartmentCreation;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.DepartmentDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.FacultyDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.FacultyRequest;
import com.facsciencesuy1.planning_management.dtos.LevelDTO;
import com.facsciencesuy1.planning_management.dtos.TeacherDTO;
import com.facsciencesuy1.planning_management.entities.Branch;
import com.facsciencesuy1.planning_management.entities.Department;
import com.facsciencesuy1.planning_management.entities.Faculty;
import com.facsciencesuy1.planning_management.entities.Level;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;

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
    private final TeacherRepository teacherRepository;

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
    public DepartmentDTO createDepartment(DepartmentCreation departmentInfos) {

        Branch branch = branchRepository.findById(departmentInfos.branchId())
                .orElseThrow(() -> new CustomBusinessException("Branch not found for this id"));
        if (departmentRepository.existsByCode(departmentInfos.code())) {
            throw new CustomBusinessException(
                    "Department with code " + departmentInfos.code() + " already exists in this branch.");
        }
        Department department = departmentRepository.save(Department.builder()
                .name(departmentInfos.name())
                .code(departmentInfos.code())
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

    @Override
    public Page<TeacherDTO> getTeachersByDepartment(String departmentId, Pageable page) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new CustomBusinessException("Department not found with id: " + departmentId));
        return teacherRepository.findByDepartmentId(departmentId, page)
                .map(t -> TeacherDTO.fromTeacher(t, department.getName(), department.getCode()));
    }
}
