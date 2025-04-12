package com.facsciences_planning_management.facsciences_planning_management.managers.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserAndRole;
import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.managers.services.AdminServices;
import com.facsciences_planning_management.facsciences_planning_management.models.RoleType;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
// @Tag(name = "Admin", description = "Endpoints for ADMIN task")
public class AdminController {

    private final AdminServices adminServices;

    @PostMapping("/create-user")
    // @Operation(summary = "Admin user creation", description = "Admin create user
    // with role")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "Creation successful"),
    // @ApiResponse(responseCode = "401", description = "Invalid informations")
    // })
    public ResponseEntity<UserResponse> createUser(@RequestBody UserAndRole userAndRole) {

        return ResponseEntity.ok().body(adminServices.createUserWithRole(userAndRole));
    }

    @GetMapping("/roles")
    // @Operation(summary = "Get Roles", description = "Find all roles of database")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "All users roles of
    // database"),
    // })
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok().body(adminServices.getAllRoles().stream().map(RoleType::name).toList());
    }

}