package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.LevelDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.FacultyService;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping("/create")
    public ResponseEntity<FacultyDTO> createFaculty(@RequestBody FacultyDTO facultyDTO) {
        return ResponseEntity.ok(facultyService.createFaculty(facultyDTO));
    }

    @PostMapping("/create/branch")
    public ResponseEntity<BranchDTO> createBranch(@RequestBody BranchRequest request) {
        return ResponseEntity.ok(facultyService.createBranch(request));
    }

    @PostMapping("/create/department")
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(facultyService.createDepartment(departmentDTO));
    }

    @PostMapping("/create/level")
    public ResponseEntity<LevelDTO> createLevel(@RequestBody LevelDTO levelDTO) {
        return ResponseEntity.ok(facultyService.createLevel(levelDTO));
    }

    @GetMapping
    public ResponseEntity<List<FacultyDTO>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{facultyId}/branches")
    public ResponseEntity<List<BranchDTO>> getBranchesByFaculty(@PathVariable String facultyId) {
        return ResponseEntity.ok(facultyService.getAllBranchesByFaculty(facultyId));
    }

    @GetMapping("/{facultyId}/departments")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByFaculty(@PathVariable String facultyId) {
        return ResponseEntity.ok(facultyService.getAllDepartmentsByFaculty(facultyId));
    }

    @GetMapping("/branches/{branchId}/levels")
    public ResponseEntity<List<LevelDTO>> getLevelsByBranch(@PathVariable String branchId) {
        return ResponseEntity.ok(facultyService.getLevelsByBranch(branchId));
    }
}
