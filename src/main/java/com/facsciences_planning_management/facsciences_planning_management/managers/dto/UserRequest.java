package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

public record UserRequest(
                String firstName,
                String lastName,
                String email,
                String password,
                String address,
                String phoneNumber) {
}
