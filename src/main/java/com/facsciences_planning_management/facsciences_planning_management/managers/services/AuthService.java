package com.facsciences_planning_management.facsciences_planning_management.managers.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

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

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ValidationRepository validationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService notificationService;

    private static final long ACTIVATION_HOURS_VALIDITY = 24 * 3600 * 1000;

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

    private void saveAndSendActivation(Users user) {
        String activationCode = generateActivationCode();
        saveActivationCode(user.getEmail(), activationCode);
        notificationService.sendActivationEmail(user.getEmail(), activationCode);
    }

    public String activate(String email, String code) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEnabled()) {
            return "User already activated";
        }

        Validation activationCode = validationRepository.findByEmailAndExpiredAfter(email, Instant.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired code"));

        if (!activationCode.getActivationCode().equals(code)) {
            return "Activation Failed";
        }

        user.setEnabled(true);
        userRepository.save(user);
        return "User activated successfully";
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

    private String generateActivationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(code);
    }

    private void saveActivationCode(String email, String code) {
        validationRepository.findByEmail(email).ifPresent(validationRepository::delete);

        Validation activationCode = new Validation();
        activationCode.setEmail(email);
        activationCode.setActivationCode(code);
        activationCode.setExpired(Instant.now().plusMillis(ACTIVATION_HOURS_VALIDITY));
        validationRepository.save(activationCode);
    }

    @Scheduled(cron = "@daily")
    public void cleanupExpiredCodes() {
        validationRepository.deleteByExpiredBefore(Instant.now());
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
