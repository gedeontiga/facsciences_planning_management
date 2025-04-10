package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

import com.facsciences_planning_management.facsciences_planning_management.models.Role;
import com.facsciences_planning_management.facsciences_planning_management.models.Users;

public record UserAndRole(
        String firstName,
        String lastName,
        String email,
        String password,
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
                .password(password)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
    }
}
