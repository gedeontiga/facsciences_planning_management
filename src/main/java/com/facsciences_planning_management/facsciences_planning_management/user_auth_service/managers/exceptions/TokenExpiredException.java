package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
