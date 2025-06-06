package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserUpdate;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public UserResponse getUser() {
        return userService.getUser();
    }

    @PostMapping("/update")
    public UserResponse updateUser(@RequestBody UserUpdate user) {
        return userService.updateUser(user);
    }

}
