package com.facsciences_planning_management.facsciences_planning_management.managers.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserRequest;
import com.facsciences_planning_management.facsciences_planning_management.managers.services.AuthService;
import com.facsciences_planning_management.facsciences_planning_management.managers.dto.LoginRequest;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import org.springframework.http.ResponseEntity;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;

    @GetMapping("/check/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(authService.isEmailAlreadyExists(email));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registration successful. Check your email for activation code.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String email, @RequestParam String token) {
        authService.activate(email, token);
        return ResponseEntity.ok("User activated successfully");
    }
}
