package com.facsciencesuy1.planning_management.user_management_service.utils.dtos;

import com.facsciencesuy1.planning_management.entities.Users;

public record UserDTO(String userId, String firstName, String lastName, String email, String role, String address,
		String phoneNumber) {
	public UserDTO(Users user) {
		this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole().getType().name(),
				user.getAddress(), user.getPhoneNumber());
	}
}
