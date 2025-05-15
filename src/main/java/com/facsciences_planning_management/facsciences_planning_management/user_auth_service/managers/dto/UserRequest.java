package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dto;

public record UserRequest(String email, String firstName, String lastName, String address, String phoneNumber,
        String password) {
}