package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.entities.Role;

public record UserAndRole(
        String firstName,
        String lastName,
        String email,
        String role,
        String address,
        String phoneNumber) {
    public Users fromUserAndRole(Role role) {
        return Users.builder()
                .address(address)
                .firstName(firstName)
                .enabled(true)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
    }
}
