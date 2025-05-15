package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto.UserRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.AccountNotActivatedException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.EmailAlreadyExistsException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.InvalidCredentialsException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.InvalidTokenException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.TokenExpiredException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.UserNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto.LoginRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto.PasswordResetRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.repositories.ValidationRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Validation;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private static final long ACTIVATION_HOURS_VALIDITY = 24 * 3600 * 1000;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ValidationRepository validationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService notificationService;

    public Boolean isEmailAlreadyExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void register(UserRequest request) {
        validateEmailUniqueness(request.email());
        Users user = Users.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .role(getRoleByType(RoleType.STUDENT))
                .enabled(false)
                .build();

        String token = generateActivationToken();
        saveToken(userRepository.save(user), token);
        notificationService.sendActivationEmail(user.getEmail(), token);
    }

    public void activate(String token) {
        Validation activation = validationRepository
                .findByActivationTokenAndExpiredIsAfter(token, Instant.now())
                .orElseThrow(() -> new TokenExpiredException("Invalid or expired activation link"));

        if (!activation.getActivationToken().equals(token)) {
            throw new InvalidTokenException("Activation Failed: invalid token");
        }

        Users user = activation.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        validationRepository.delete(activation);
    }

    public Map<String, String> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                return jwtService.generate(request.email());
            }
            throw new InvalidCredentialsException("Invalid login credentials");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid login credentials");
        } catch (DisabledException e) {
            throw new AccountNotActivatedException("Account is not activated");
        } catch (Exception e) {
            throw new InvalidCredentialsException("Authentication failed: " + e.getMessage());
        }
    }

    public void requestPasswordReset(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!user.isEnabled()) {
            throw new AccountNotActivatedException("Account not activated");
        }

        String token = generateActivationToken();
        saveToken(user, token);
        notificationService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(PasswordResetRequest request) {
        Validation resetValidation = validationRepository
                .findByActivationTokenAndExpiredIsAfter(request.token(), Instant.now())
                .orElseThrow(() -> new TokenExpiredException("Invalid or expired reset token"));

        Users user = resetValidation.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        validationRepository.delete(resetValidation);
    }

    @Scheduled(cron = "@daily")
    public void cleanupExpiredTokens() {
        validationRepository.deleteByExpiredBefore(Instant.now());
    }

    private String generateActivationToken() {
        return UUID.randomUUID().toString();
    }

    private void saveToken(Users user, String token) {
        Validation activation = new Validation();
        activation.setUser(user);
        activation.setActivationToken(token);
        activation.setExpired(Instant.now().plusMillis(ACTIVATION_HOURS_VALIDITY));
        validationRepository.save(activation);
    }

    private Role getRoleByType(RoleType roleType) {
        return roleRepository.findByType(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType));
    }

    private void validateEmailUniqueness(String email) {
        if (isEmailAlreadyExists(email)) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
    }
}