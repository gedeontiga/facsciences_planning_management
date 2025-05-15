package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto.UserAndRole;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto.UserResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.repositories.RoleRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.RoleType;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Users;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AdminServices {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserResponse createUserWithRole(UserAndRole userAndRole) {
        Role role = roleRepository.findByType(RoleType.valueOf(userAndRole.role()))
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Users user = userAndRole.fromUserAndRole(role);
        return new UserResponse(userRepository.save(user));
    }

    public List<RoleType> getAllRoles() {
        return roleRepository.findAll().stream().map(Role::getType).collect(Collectors.toList());
    }
}
