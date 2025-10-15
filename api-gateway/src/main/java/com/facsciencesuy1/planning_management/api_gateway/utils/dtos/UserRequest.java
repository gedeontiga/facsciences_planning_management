package com.facsciencesuy1.planning_management.api_gateway.utils.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRequest(@Email String email, String firstName, String lastName, String address, String phoneNumber,
		@Size(min = 8) String password, String levelId) {
}