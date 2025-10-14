package com.facsciencesuy1.planning_management.dtos;

import com.facsciencesuy1.planning_management.entities.Role;

public record RoleDTO(String id, String name) {
    public static RoleDTO fromRole(Role role) {
        return new RoleDTO(role.getId(), role.getType().name());
    }
}
