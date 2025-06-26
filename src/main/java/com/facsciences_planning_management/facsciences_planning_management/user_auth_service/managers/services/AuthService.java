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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Validation;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.ValidationRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.LoginRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.PasswordResetRequest;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserRequest;

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
                .orElseThrow(() -> new CustomBusinessException("Invalid or expired activation link"));

        if (!activation.getActivationToken().equals(token)) {
            throw new CustomBusinessException("Activation Failed: invalid token");
        }

        Users user = activation.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        validationRepository.delete(activation);
    }

    public Map<String, String> login(LoginRequest request) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                return jwtService.generate(request.email());
            }
            throw new BadCredentialsException("Invalid login credentials");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid login credentials");
        } catch (DisabledException e) {
            throw new BadCredentialsException("User not found or account is not activated");
        } catch (Exception e) {
            throw new BadCredentialsException("Authentication failed: invalid login credentials");
        }
    }

    public void requestPasswordReset(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomBusinessException("User not found with email: " + email));

        if (!user.isEnabled()) {
            throw new CustomBusinessException("Account not activated");
        }

        String token = generateActivationToken();
        saveToken(user, token);
        notificationService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(PasswordResetRequest request) {
        Validation resetValidation = validationRepository
                .findByActivationTokenAndExpiredIsAfter(request.token(), Instant.now())
                .orElseThrow(() -> new CustomBusinessException("Invalid or expired reset token"));

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
                .orElseThrow(() -> new CustomBusinessException("Role not found: " + roleType));
    }

    private void validateEmailUniqueness(String email) {
        if (isEmailAlreadyExists(email)) {
            throw new CustomBusinessException("Email already registered");
        }
    }
}