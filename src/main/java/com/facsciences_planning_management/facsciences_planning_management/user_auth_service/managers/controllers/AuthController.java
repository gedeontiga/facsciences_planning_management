package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.controllers;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.LoginRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.LoginResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.PasswordResetRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services.AuthService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirements()
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).body("User registered successfully. Please check your email for activation.");
    }

    @PatchMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) {
        authService.activate(token);
        return ResponseEntity.ok("Account activated successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws AuthenticationException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful");
    }
}