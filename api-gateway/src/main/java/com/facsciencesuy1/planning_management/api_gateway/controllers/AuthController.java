package com.facsciencesuy1.planning_management.api_gateway.controllers;

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

import com.facsciencesuy1.planning_management.api_gateway.dtos.LoginRequest;
import com.facsciencesuy1.planning_management.api_gateway.dtos.LoginResponse;
import com.facsciencesuy1.planning_management.api_gateway.dtos.PasswordResetRequest;
import com.facsciencesuy1.planning_management.api_gateway.dtos.UserRequest;
import com.facsciencesuy1.planning_management.api_gateway.services.AuthService;

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