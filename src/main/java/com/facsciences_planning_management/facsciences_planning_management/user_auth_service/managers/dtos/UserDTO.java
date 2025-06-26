package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;

public record UserDTO(
		String userId,
		String firstName,
		String lastName,
		String email,
		String role,
		String address,
		String phoneNumber) {
	public UserDTO(Users user) {
		this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
				user.getRole().getType().name(), user.getAddress(), user.getPhoneNumber());
	}
}
