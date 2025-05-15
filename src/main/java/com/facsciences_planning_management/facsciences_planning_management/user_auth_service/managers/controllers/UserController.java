package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services.UserService;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public UserResponse getUser() {
        String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userService.getUser(email);
    }

}
