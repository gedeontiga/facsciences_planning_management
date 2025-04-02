package com.facsciences_planning_management.facsciences_planning_management.managers.services;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.UserRequest;
import com.facsciences_planning_management.facsciences_planning_management.managers.dto.LoginRequest;
import com.facsciences_planning_management.facsciences_planning_management.managers.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.managers.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.managers.repositories.ValidationRepository;
import com.facsciences_planning_management.facsciences_planning_management.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.models.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.models.Users;
import com.facsciences_planning_management.facsciences_planning_management.models.Validation;

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
        Users customer = Users.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .role(getRoleByType(RoleType.STUDENT))
                .enabled(false)
                .build();

        saveAndSendActivation(userRepository.save(customer));
    }

    public void activate(String token) {

        Validation activation = validationRepository.findByActivationTokenAndExpiredIsAfter(token, Instant.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired activation link"));

        if (!activation.getActivationToken().equals(token)) {
            throw new RuntimeException("Activation Failed: invalid token");
        }

        Users user = activation.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        validationRepository.delete(activation); // Clean up the used token
    }

    public Map<String, String> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (authentication.isAuthenticated()) {
            return jwtService.generate(request.email());
        }
        throw new RuntimeException("Invalid login credentials");
    }

    @Scheduled(cron = "@daily")
    public void cleanupExpiredTokens() {
        validationRepository.deleteByExpiredBefore(Instant.now());
    }

    private void saveAndSendActivation(Users user) {
        String token = generateActivationToken();
        saveActivationToken(user, token);
        notificationService.sendActivationEmail(user.getEmail(), token);
    }

    private String generateActivationToken() {
        return UUID.randomUUID().toString();
    }

    private void saveActivationToken(Users user, String token) {

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
            throw new RuntimeException("Email already registered");
        }
    }
}
