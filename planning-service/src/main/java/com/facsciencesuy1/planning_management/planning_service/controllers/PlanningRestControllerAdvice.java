package com.facsciencesuy1.planning_management.planning_service.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.facsciencesuy1.planning_management.advices.GlobalExceptionHandler;
import com.facsciencesuy1.planning_management.dtos.ErrorResponse;
import com.facsciencesuy1.planning_management.planning_service.utils.exceptions.SchedulingConflictException;

@RestControllerAdvice
public class PlanningRestControllerAdvice extends GlobalExceptionHandler {
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
