package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.RoleDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services.AdminServices;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminServices adminServices;

    @PostMapping("/create-user")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok().body(adminServices.createUserWithRole(user));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllTeachers(
            @RequestParam String roleId,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok().body(adminServices.getUserByRole(roleId, page));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok().body(adminServices.getAllRoles());
    }

}