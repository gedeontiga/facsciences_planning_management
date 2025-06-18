package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.RoleDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserAndRole;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServices {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService mailNotificationService;

    public UserResponse createUserWithRole(UserAndRole userAndRole) {
        // Find role
        Role role = roleRepository.findByType(RoleType.valueOf(userAndRole.role()))
                .orElseThrow(() -> new RuntimeException("Role not found"));
        final String DEFAULT_PASSWORD = userAndRole.firstName().toLowerCase() + ".password123!";

        // Create user with encoded default password
        Users user = Users.builder()
                .firstName(userAndRole.firstName())
                .lastName(userAndRole.lastName())
                .email(userAndRole.email())
                .address(userAndRole.address())
                .phoneNumber(userAndRole.phoneNumber())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(role)
                .enabled(true)
                .build();

        log.debug(user.toString());

        // Save user
        Users savedUser = userRepository.save(user);

        // Send account creation notification
        try {
            mailNotificationService.sendAccountCreationEmail(
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    DEFAULT_PASSWORD,
                    role.getType().toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send account creation email:" + e.getMessage(), e.getCause());
        }

        return new UserResponse(savedUser);
    }

    public List<UserResponse> getUserByRole(String roleId) {
        return userRepository.findByRoleId(roleId).stream().map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(role -> role.toDTO()).collect(Collectors.toList());
    }
}
