package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException(String message) {
        super(message);
    }
}
