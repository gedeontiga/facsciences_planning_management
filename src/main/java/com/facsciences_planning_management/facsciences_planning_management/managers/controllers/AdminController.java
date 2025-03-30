package com.facsciences_planning_management.facsciences_planning_management.managers.controllers;
// package
// com.facsciences_planning_management.facsciences_planning_management.managers.controllers.auth;

// import org.springframework.web.bind.annotation.RestController;

// import
// com.facsciences_planning_management.facsciences_planning_management.managers.services.auth.AdminServices;
// import
// com.facsciences_planning_management.facsciences_planning_management.models.Users;

// import lombok.AllArgsConstructor;

// import java.util.List;

// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// @AllArgsConstructor
// @RestController
// @RequestMapping("api/admin")
// public class AdminController {

// private final AdminServices adminServices;

// @PreAuthorize("hasAuthority('ADMIN_READ_ALL')")
// @GetMapping("/all-users")
// public List<Users> getAllUsers() {
// return adminServices.getAllUsers();
// }
// }