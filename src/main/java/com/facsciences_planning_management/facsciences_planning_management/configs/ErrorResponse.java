package com.facsciences_planning_management.facsciences_planning_management.configs;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(int status, String message, Optional<Map<String, String>> errors) {
}
