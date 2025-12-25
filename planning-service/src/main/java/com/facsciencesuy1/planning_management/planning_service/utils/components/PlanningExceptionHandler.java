package com.facsciencesuy1.planning_management.planning_service.utils.components;

import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;
import com.facsciencesuy1.planning_management.planning_service.utils.exceptions.SchedulingConflictException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class PlanningExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CustomBusinessException.class)
	ProblemDetail handleBusinessException(CustomBusinessException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
		problemDetail.setTitle("Business Rule Violation");
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}

	@ExceptionHandler(Exception.class)
	ProblemDetail handleUnhandledException(Exception e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				e.getMessage());
		problemDetail.setTitle("Internal Server Error");
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}

	@ExceptionHandler(SchedulingConflictException.class)
	public ProblemDetail handleSchedulingConflictException(SchedulingConflictException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		problemDetail.setTitle("Scheduling Conflict");
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}
}
