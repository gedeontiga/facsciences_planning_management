package com.facsciences_planning_management.facsciences_planning_management.managers.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
