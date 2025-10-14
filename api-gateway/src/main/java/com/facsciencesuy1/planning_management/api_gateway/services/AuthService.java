package com.facsciencesuy1.planning_management.api_gateway.services;

import java.time.Instant;
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

import com.facsciencesuy1.planning_management.api_gateway.dtos.LoginRequest;
import com.facsciencesuy1.planning_management.api_gateway.dtos.LoginResponse;
import com.facsciencesuy1.planning_management.api_gateway.dtos.PasswordResetRequest;
import com.facsciencesuy1.planning_management.api_gateway.dtos.UserRequest;
import com.facsciencesuy1.planning_management.api_gateway.repositories.LevelRepository;
import com.facsciencesuy1.planning_management.api_gateway.repositories.RoleRepository;
import com.facsciencesuy1.planning_management.api_gateway.repositories.UserRepository;
import com.facsciencesuy1.planning_management.api_gateway.repositories.ValidationRepository;
import com.facsciencesuy1.planning_management.entities.Role;
import com.facsciencesuy1.planning_management.entities.Student;
import com.facsciencesuy1.planning_management.entities.Users;
import com.facsciencesuy1.planning_management.entities.Validation;
import com.facsciencesuy1.planning_management.entities.types.RoleType;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private static final long ACTIVATION_HOURS_VALIDITY = 24 * 3600 * 1000;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ValidationRepository validationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LevelRepository levelRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService notificationService;

    public Boolean isEmailAlreadyExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void register(UserRequest request) {
        validateRequest(request.email(), request.levelId());
        Student user = Student.builder().email(request.email()).firstName(request.firstName())
                .lastName(request.lastName()).address(request.address()).phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password())).role(getRoleByType(RoleType.STUDENT))
                .levelId(request.levelId()).enabled(false).build();

        String token = generateActivationToken();
        saveToken(userRepository.save(user), token);
        notificationService.sendActivationEmail(user.getEmail(), token);
    }

    public void activate(String token) {
        Validation activation = validationRepository.findByActivationTokenAndExpiredIsAfter(token, Instant.now())
                .orElseThrow(() -> new CustomBusinessException("Invalid or expired activation link"));

        if (!activation.getActivationToken().equals(token)) {
            throw new CustomBusinessException("Activation Failed: invalid token");
        }

        Users user = activation.getUser();
        userRepository.findByEmail(user.getEmail()).ifPresent(updatedUser -> {
            updatedUser.setEnabled(true);
            userRepository.save(updatedUser);
        });
        validationRepository.delete(activation);
    }

    public LoginResponse login(LoginRequest request) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                Users user = (Users) authentication.getPrincipal();
                return jwtService.generate(request.email(), user.getRole().getType());
            }
            throw new BadCredentialsException("Invalid login credentials");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid login credentials");
        } catch (DisabledException e) {
            throw new BadCredentialsException("User not found or account is not activated");
        } catch (Exception e) {
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
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
        userRepository.findByEmail(user.getEmail()).ifPresent(updatedUser -> {
            updatedUser.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(updatedUser);
        });
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

    private void validateRequest(String email, String levelId) {
        if (isEmailAlreadyExists(email)) {
            throw new CustomBusinessException("Email already registered");
        }

        if (!levelRepository.existsById(levelId)) {
            throw new CustomBusinessException("Level not found");
        }
    }
}