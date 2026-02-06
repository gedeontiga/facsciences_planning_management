package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Reactive Exception Handler for WebFlux
 * 
 * Handles exceptions from reactive controllers and returns RFC 7807 Problem
 * Details.
 * This handler catches exceptions that occur in reactive streams (Mono/Flux).
 */
@Slf4j
@RestControllerAdvice
public class ReactiveExceptionHandler {

    /**
     * Handle bad credentials (wrong email/password)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password");
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "AUTH_001");

        return Mono.just(problemDetail);
    }

    /**
     * Handle disabled account (account not activated)
     */
    @ExceptionHandler(DisabledException.class)
    public Mono<ProblemDetail> handleDisabledException(DisabledException ex) {
        log.warn("Account disabled: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Account is not activated. Please check your email for activation link.");
        problemDetail.setTitle("Account Not Activated");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "AUTH_002");

        return Mono.just(problemDetail);
    }

    /**
     * Handle JWT exceptions (expired token, invalid token, etc.)
     */
    @ExceptionHandler(JwtException.class)
    public Mono<ProblemDetail> handleJwtException(JwtException ex) {
        log.warn("JWT error: {}", ex.getMessage());

        String detail = "Invalid or expired authentication token";
        String errorCode = "AUTH_003";

        // Specific error messages based on exception message
        if (ex.getMessage().contains("expired")) {
            detail = "Authentication token has expired. Please login again.";
            errorCode = "AUTH_004";
        } else if (ex.getMessage().contains("Invalid token format")) {
            detail = "Invalid token format";
            errorCode = "AUTH_005";
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                detail);
        problemDetail.setTitle("Token Validation Failed");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", errorCode);

        return Mono.just(problemDetail);
    }

    /**
     * Handle business logic exceptions
     */
    @ExceptionHandler(CustomBusinessException.class)
    public Mono<ProblemDetail> handleBusinessException(CustomBusinessException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "BUS_001");

        return Mono.just(problemDetail);
    }

    /**
     * Handle validation errors (e.g., @Valid annotations)
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ProblemDetail> handleValidationException(WebExchangeBindException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        // Collect all validation errors
        StringBuilder errors = new StringBuilder();
        ex.getFieldErrors().forEach(error -> errors.append(error.getField())
                .append(": ")
                .append(error.getDefaultMessage())
                .append("; "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                errors.toString());
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "VAL_001");
        problemDetail.setProperty("fieldErrors", ex.getFieldErrors());

        return Mono.just(problemDetail);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "SYS_001");

        // Don't expose internal error details to client in production
        // Only log them for debugging

        return Mono.just(problemDetail);
    }
}