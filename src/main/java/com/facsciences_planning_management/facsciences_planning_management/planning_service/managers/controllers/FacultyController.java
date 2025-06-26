package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.LevelDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.FacultyService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

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
