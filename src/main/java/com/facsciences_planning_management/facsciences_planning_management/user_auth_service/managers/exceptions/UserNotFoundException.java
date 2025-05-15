package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
