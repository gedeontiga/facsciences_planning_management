package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos;

public class AuthErrorResponse {
    private int statusCode;
    private String message;

    public AuthErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
