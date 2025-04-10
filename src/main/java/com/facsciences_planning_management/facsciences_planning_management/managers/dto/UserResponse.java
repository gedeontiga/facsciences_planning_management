package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

import com.facsciences_planning_management.facsciences_planning_management.models.Users;

public record UserResponse(
                String userId,
                String firstName,
                String lastName,
                String email,
                String address,
                String phoneNumber) {
        public UserResponse(Users user) {
                this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                                user.getAddress(), user.getPhoneNumber());
        }
}
