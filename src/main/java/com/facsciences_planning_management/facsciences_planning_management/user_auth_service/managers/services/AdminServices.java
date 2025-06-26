package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.entities.types.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.RoleDTO;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.UserDTO;

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

    public UserDTO createUserWithRole(UserDTO userInfo) {
        // Find role
        Role role = roleRepository.findByType(RoleType.valueOf(userInfo.role()))
                .orElseThrow(() -> new RuntimeException("Role not found"));
        final String DEFAULT_PASSWORD = userInfo.firstName().toLowerCase() + ".password123!";

        // Create user with encoded default password
        Users user = Users.builder()
                .firstName(userInfo.firstName())
                .lastName(userInfo.lastName())
                .email(userInfo.email())
                .address(userInfo.address())
                .phoneNumber(userInfo.phoneNumber())
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

        return new UserDTO(savedUser);
    }

    public Page<UserDTO> getUserByRole(String roleId, Pageable page) {
        Page<Users> userPage = userRepository.findByRoleId(roleId, page);
        return userPage.map(UserDTO::new);
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(role -> role.toDTO()).collect(Collectors.toList());
    }
}
