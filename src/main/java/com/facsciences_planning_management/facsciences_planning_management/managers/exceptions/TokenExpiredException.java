package com.facsciences_planning_management.facsciences_planning_management.managers.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
