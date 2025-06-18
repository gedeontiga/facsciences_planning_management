package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;

public record RoleDTO(
        String id,
        String name) {
    public static RoleDTO fromRole(Role role) {
        return new RoleDTO(role.getId(), role.getType().name());
    }
}
