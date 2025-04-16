package com.facsciences_planning_management.facsciences_planning_management.managers.dto;

public record UserRequest(String email, String firstName, String lastName, String address, String phoneNumber,
                String password) {
}