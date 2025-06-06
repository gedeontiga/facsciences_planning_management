package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;

public record UserDTO(
        String id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String roleId) {
    public static UserDTO fromUser(Users user) {
        return new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getPhoneNumber(), user.getRole().getId());
    }
}
