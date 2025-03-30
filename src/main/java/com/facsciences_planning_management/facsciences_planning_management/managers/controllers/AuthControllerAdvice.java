package com.facsciences_planning_management.facsciences_planning_management.managers.controllers;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.facsciences_planning_management.facsciences_planning_management.managers.dto.ErrorResponse;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.ActivationCodeException;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.EmailAlreadyExistsException;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.TokenExpiredException;
import com.facsciences_planning_management.facsciences_planning_management.managers.exceptions.UserNotFoundException;

@ControllerAdvice
public class AuthControllerAdvice {

    // Handle User Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handle Token Expired
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // Handle Activation Code Issues
    @ExceptionHandler(ActivationCodeException.class)
    public ResponseEntity<ErrorResponse> handleActivationCodeException(ActivationCodeException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle Email Already Exists
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex,
            WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
