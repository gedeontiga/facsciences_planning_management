package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
