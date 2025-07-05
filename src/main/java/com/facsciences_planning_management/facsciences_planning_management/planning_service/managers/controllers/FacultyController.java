package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

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

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.BranchRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.DepartmentDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.FacultyRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.LevelDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.FacultyService;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.TeacherDTO;

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
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(facultyService.createDepartment(departmentDTO));
    }

    @PostMapping("/create/level")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LevelDTO> createLevel(@Valid @RequestBody LevelDTO levelDTO) {
        return ResponseEntity.ok(facultyService.createLevel(levelDTO));
    }

    @PatchMapping("/update/level/{levelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DEPARTMENT_HEAD', 'SECRETARY')")
    public ResponseEntity<LevelDTO> updateLevel(@NonNull @PathVariable String levelId,
            @Valid @RequestBody @NonNull Long headCount) {
        return ResponseEntity.ok(facultyService.updateLevel(levelId, headCount));
    }

    @GetMapping
    public ResponseEntity<List<FacultyDTO>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{facultyId}/branches")
    public ResponseEntity<List<BranchDTO>> getBranchesByFaculty(@NonNull @PathVariable String facultyId) {
        return ResponseEntity.ok(facultyService.getAllBranchesByFaculty(facultyId));
    }

    @GetMapping("/{facultyId}/departments")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByFaculty(@NonNull @PathVariable String facultyId) {
        return ResponseEntity.ok(facultyService.getAllDepartmentsByFaculty(facultyId));
    }

    @GetMapping("/branches/{branchId}/levels")
    public ResponseEntity<List<LevelDTO>> getLevelsByBranch(@NonNull @PathVariable String branchId) {
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
