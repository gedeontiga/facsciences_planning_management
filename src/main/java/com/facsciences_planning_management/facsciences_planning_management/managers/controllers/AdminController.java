package com.facsciences_planning_management.facsciences_planning_management.managers.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserAndRole;
import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.managers.services.AdminServices;
import com.facsciences_planning_management.facsciences_planning_management.models.RoleType;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminServices adminServices;

    @PostMapping("/create-user")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserAndRole userAndRole) {

        return ResponseEntity.ok().body(adminServices.createUserWithRole(userAndRole));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok().body(adminServices.getAllRoles().stream().map(RoleType::name).toList());
    }

}