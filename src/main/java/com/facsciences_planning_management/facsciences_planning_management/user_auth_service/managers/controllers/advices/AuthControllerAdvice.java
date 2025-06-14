package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.controllers.advices;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.EntityNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.dtos.AuthErrorResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.AccountNotActivatedException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.ActivationCodeException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.EmailAlreadyExistsException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.InvalidCredentialsException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.InvalidTokenException;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.exceptions.TokenExpiredException;

@ControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<AuthErrorResponse> handleUserNotFoundException(EntityNotFoundException ex,
            WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<AuthErrorResponse> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ActivationCodeException.class)
    public ResponseEntity<AuthErrorResponse> handleActivationCodeException(ActivationCodeException ex,
            WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<AuthErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex,
            WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex,
            WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<AuthErrorResponse> handleAccountNotActivatedException(AccountNotActivatedException ex,
            WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
            WebRequest request) {
        return buildErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AuthErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse("Access denied", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthErrorResponse> handleAuthenticationException(AuthenticationException ex,
            WebRequest request) {
        return buildErrorResponse("Authentication failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        // Log the exception for debugging
        ex.printStackTrace();
        return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<AuthErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        AuthErrorResponse errorResponse = new AuthErrorResponse(status.value(), message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
