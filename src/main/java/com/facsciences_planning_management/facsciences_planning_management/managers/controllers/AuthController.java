package com.facsciences_planning_management.facsciences_planning_management.managers.controllers;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.LoginRequest;
import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserRequest;
import com.facsciences_planning_management.facsciences_planning_management.managers.dto.PasswordResetRequest;
import com.facsciences_planning_management.facsciences_planning_management.managers.services.AuthService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
// @Tag(name = "Authentication", description = "Endpoints for user
// authentication and account management")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    // @Operation(summary = "Register a new user", description = "Creates a new user
    // account and sends an activation email")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "User registered
    // successfully"),
    // @ApiResponse(responseCode = "400", description = "Invalid request data or
    // email already exists")
    // })
    public ResponseEntity<String> register(@RequestBody UserRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully. Please check your email for activation.");
    }

    @GetMapping("/activate")
    // @Operation(summary = "Activate user account", description = "Activates a user
    // account using the provided token")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "Account activated
    // successfully"),
    // @ApiResponse(responseCode = "400", description = "Invalid or expired
    // activation token")
    // })
    public ResponseEntity<String> activate(@RequestParam String token) {
        authService.activate(token);
        return ResponseEntity.ok("Account activated successfully");
    }

    @PostMapping("/login")
    // @Operation(summary = "User login", description = "Authenticates a user and
    // returns a JWT token")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "Login successful, returns
    // JWT token"),
    // @ApiResponse(responseCode = "401", description = "Invalid credentials")
    // })
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reset-password-request")
    // @Operation(summary = "Request password reset", description = "Sends a
    // password reset email with a token")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "Password reset email
    // sent"),
    // @ApiResponse(responseCode = "404", description = "User not found")
    // })
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    // @Operation(summary = "Reset password", description = "Resets the user's
    // password using the provided token")
    // @ApiResponses({
    // @ApiResponse(responseCode = "200", description = "Password reset
    // successful"),
    // @ApiResponse(responseCode = "400", description = "Invalid or expired reset
    // token")
    // })
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful");
    }
}