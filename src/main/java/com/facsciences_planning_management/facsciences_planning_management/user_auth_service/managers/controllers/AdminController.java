package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.RoleDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.TeacherDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services.AdminServices;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminServices adminServices;

    @PostMapping("/users/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok().body(adminServices.createUserWithRole(user));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllByRole(
            @RequestParam String roleId,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok().body(adminServices.getUserByRole(roleId, page));
    }

    @GetMapping("/users/{type}")
    public ResponseEntity<Page<UserDTO>> getAllByRole(
            @PathVariable RoleType type,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok().body(adminServices.getUserByRole(type, page));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok().body(adminServices.getAllRoles());
    }

    @PutMapping("/users/disable/{id}")
    public ResponseEntity<String> putMethodName(@PathVariable String id, @RequestBody String entity) {
        adminServices.disableUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        adminServices.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teachers/department/{departmentId}")
    public ResponseEntity<Page<TeacherDTO>> getTeachersByDepartment(
            @PathVariable String departmentId,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(adminServices.getTeachersByDepartment(departmentId, page));
    }

}