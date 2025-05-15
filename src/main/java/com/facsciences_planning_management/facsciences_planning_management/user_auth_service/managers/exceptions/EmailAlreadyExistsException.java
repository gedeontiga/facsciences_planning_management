package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
