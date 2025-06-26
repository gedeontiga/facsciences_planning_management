package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.BranchRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.DepartmentRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.FacultyRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.LevelDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.FacultyService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final BranchRepository branchRepository;
    private final LevelRepository levelRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<LevelDTO> getLevelsByBranch(String branchId) {
        log.info("Fetching levels for branch with ID: {}", branchId);
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
        log.info("Fetching all branches for faculty with ID: {}", facultyId);
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
        log.info("Fetching all departments for faculty with ID: {}", facultyId);
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
        log.info("Fetching all faculties");
        return facultyRepository.findAll()
                .stream()
                .map(FacultyDTO::fromFaculty)
                .collect(Collectors.toList());
    }
}
