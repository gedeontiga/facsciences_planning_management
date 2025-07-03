package com.facsciences_planning_management.facsciences_planning_management.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.facsciences_planning_management.facsciences_planning_management.dto.ErrorResponse;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.exception.SchedulingConflictException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Invalid Argument",
				ex.getMessage(),
				HttpStatus.BAD_REQUEST.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Data Integrity Error",
				ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		String message = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.collect(Collectors.joining("; "));

		ErrorResponse errorResponse = new ErrorResponse(
				"Validation Error",
				message,
				HttpStatus.BAD_REQUEST.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Keep existing exception handlers...
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
			HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
				"Access Denied",
				ex.getMessage(),
				HttpStatus.FORBIDDEN.value(),
				LocalDateTime.now().toString());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
				"Internal Server Error",
				ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				LocalDateTime.now().toString());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));

		ErrorResponse errorResponse = new ErrorResponse(
				"Validation Error",
				message,
				HttpStatus.BAD_REQUEST.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponse> handleInsufficientAuthenticationException(
			InsufficientAuthenticationException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Authentication Error",
				"Authentication credentials are required",
				HttpStatus.UNAUTHORIZED.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	// Handle Spring Security authentication exceptions
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Authentication Error",
				ex.getMessage(),
				HttpStatus.UNAUTHORIZED.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(JwtException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ErrorResponse> handleAccessTokenException(JwtException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Access Denied",
				ex.getMessage(),
				HttpStatus.FORBIDDEN.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	// Handle custom business exceptions (if any)
	@ExceptionHandler(CustomBusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleCustomBusinessException(CustomBusinessException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Business Error",
				ex.getMessage(),
				HttpStatus.BAD_REQUEST.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(SchedulingConflictException.class)
	public ResponseEntity<ErrorResponse> handleSchedulingConflictException(SchedulingConflictException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				"Scheduling Conflict",
				ex.getMessage(),
				HttpStatus.CONFLICT.value(),
				LocalDateTime.now().toString());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}
}