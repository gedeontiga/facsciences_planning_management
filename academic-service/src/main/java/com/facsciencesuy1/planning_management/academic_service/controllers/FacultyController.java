package com.facsciencesuy1.planning_management.academic_service.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciencesuy1.planning_management.academic_service.services.interfaces.FacultyService;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.BranchDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.BranchRequest;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.DepartmentCreation;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.DepartmentDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.FacultyDTO;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.FacultyRequest;
import com.facsciencesuy1.planning_management.dtos.LevelDTO;
import com.facsciencesuy1.planning_management.dtos.TeacherDTO;

import jakarta.validation.Valid;

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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FacultyDTO> createFaculty(@Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(facultyService.createFaculty(request));
    }

    @PostMapping("/create/branch")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BranchDTO> createBranch(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(facultyService.createBranch(request));
    }

    @PostMapping("/create/department")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentCreation department) {
        return ResponseEntity.ok(facultyService.createDepartment(department));
    }

    @PostMapping("/create/level")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LevelDTO> createLevel(@Valid @RequestBody LevelDTO levelDTO) {
        return ResponseEntity.ok(facultyService.createLevel(levelDTO));
    }

    @PatchMapping("/update/level/{levelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD', 'SECRETARY')")
    public ResponseEntity<LevelDTO> updateLevel(@PathVariable String levelId,
            @Valid @RequestBody LevelHeadCountUpdate request) {
        return ResponseEntity.ok(facultyService.updateLevel(levelId, request.headCount()));
    }

    private record LevelHeadCountUpdate(
            @NonNull Long headCount) {
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

    @GetMapping("/teachers/department/{departmentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD', 'SECRETARY')")
    public ResponseEntity<Page<TeacherDTO>> getTeachersByDepartment(
            @PathVariable String departmentId,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(facultyService.getTeachersByDepartment(departmentId, page));
    }
}
