package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRequest(
		@Email String email,
		String firstName,
		String lastName,
		String address,
		String phoneNumber,
		@Size(min = 8) String password,
		String levelId) {
}