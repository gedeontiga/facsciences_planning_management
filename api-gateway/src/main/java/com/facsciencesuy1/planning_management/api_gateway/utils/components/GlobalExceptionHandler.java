package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import com.facsciencesuy1.planning_management.dtos.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        // Check if response is already committed
        if (exchange.getResponse().isCommitted()) {
            log.error("Response already committed, cannot handle error: {}", ex.getMessage());
            return Mono.error(ex);
        }

        log.error("Error processing request: {}", ex.getMessage(), ex);

        HttpStatus status = determineHttpStatus(ex);
        String error = determineError(ex);
        String message = determineMessage(ex);

        ErrorResponse errorResponse = new ErrorResponse(
                error,
                message,
                status.value(),
                LocalDateTime.now().toString());

        // Set status and headers before writing body
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            bytes = "{\"error\":\"Internal Server Error\",\"message\":\"Error serializing response\",\"statusCode\":500}"
                    .getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return HttpStatus.resolve(((ResponseStatusException) ex).getStatusCode().value());
        }
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof io.jsonwebtoken.JwtException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof org.springframework.security.core.AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof org.springframework.web.server.ServerWebInputException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineError(Throwable ex) {
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            return "Authentication Error";
        }
        if (ex instanceof io.jsonwebtoken.JwtException) {
            return "Access Denied";
        }
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            return "Access Denied";
        }
        if (ex instanceof org.springframework.security.core.AuthenticationException) {
            return "Authentication Error";
        }
        if (ex instanceof org.springframework.web.server.ServerWebInputException) {
            return "Validation Error";
        }
        return "Internal Server Error";
    }

    private String determineMessage(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            String reason = ((ResponseStatusException) ex).getReason();
            return reason != null ? reason : ex.getMessage();
        }
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            return "Token has expired";
        }
        if (ex instanceof io.jsonwebtoken.JwtException) {
            String message = ex.getMessage();
            return message != null ? message : "Invalid token";
        }
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            return "Access denied";
        }
        if (ex instanceof org.springframework.security.core.AuthenticationException) {
            return "Authentication credentials are required";
        }
        if (ex instanceof org.springframework.security.authentication.BadCredentialsException) {
            return ex.getMessage();
        }
        return ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
    }
}