package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {
        public ResourceConflictException(String message) {
                super(message);
        }
}