package com.facsciencesuy1.planning_management.user_management_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facsciencesuy1.planning_management.user_management_service.dtos.UserDTO;
import com.facsciencesuy1.planning_management.user_management_service.dtos.UserUpdate;
import com.facsciencesuy1.planning_management.user_management_service.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public UserDTO getUser() {
        return userService.getUser();
    }

    @PostMapping("/update")
    public UserDTO updateUser(@RequestBody UserUpdate user) {
        return userService.updateUser(user);
    }

}
