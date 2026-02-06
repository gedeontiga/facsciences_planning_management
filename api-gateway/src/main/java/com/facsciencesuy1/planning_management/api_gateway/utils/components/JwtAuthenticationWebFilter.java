package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * JWT Authentication Filter for Spring WebFlux
 * 
 * This filter:
 * 1. Extracts JWT token from Authorization header
 * 2. Validates the token
 * 3. Sets authentication context for authenticated requests
 * 4. Returns RFC 7807 Problem Details for auth errors
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Allow public paths without authentication
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Check for missing authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Missing or invalid Authorization header for path: {}", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "Missing Authentication Token",
                    "Authorization header with Bearer token is required",
                    "AUTH_010");
        }

        String jwt = authHeader.substring(7);

        // Validate token format
        if (!isValidTokenFormat(jwt)) {
            log.warn("Invalid token format for path: {}", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "Invalid Token Format",
                    "JWT token must have three parts separated by dots",
                    "AUTH_011");
        }

        // Validate token using JwtService
        try {
            if (jwtService.isTokenExpired(jwt)) {
                log.warn("Expired token for path: {}", path);
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                        "Token Expired",
                        "Your authentication token has expired. Please login again.",
                        "AUTH_012");
            }

            if (!jwtService.isTokenValid(jwt)) {
                log.warn("Invalid token for path: {}", path);
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                        "Invalid Token",
                        "Authentication token is invalid or has been revoked",
                        "AUTH_013");
            }

            String email = jwtService.getEmailFromToken(jwt);

            if (email == null || email.trim().isEmpty()) {
                log.warn("Token missing email claim for path: {}", path);
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                        "Invalid Token Claims",
                        "Token is missing required user information",
                        "AUTH_014");
            }

            // Load user details and authenticate BEFORE continuing the chain
            return userDetailsService.findByUsername(email)
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("User not found: {} for path: {}", email, path);
                        return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                                "User Not Found",
                                "No user account found for this token",
                                "AUTH_015")
                                .then(Mono.empty());
                    }))
                    .flatMap(userDetails -> {
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        log.debug("Authenticated user: {} with roles: {}", email, userDetails.getAuthorities());

                        // Continue the chain with authentication context
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                    });

        } catch (JwtException e) {
            log.warn("JWT validation failed for path {}: {}", path, e.getMessage());
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "Token Validation Failed",
                    e.getMessage(),
                    "AUTH_016");
        } catch (Exception e) {
            log.error("Unexpected error during authentication for path {}: {}", path, e.getMessage(), e);
            return sendErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Authentication Error",
                    "An unexpected error occurred during authentication",
                    "AUTH_099");
        }
    }

    /**
     * Check if path is public and doesn't require authentication
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/actuator/info") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/ws/");
    }

    /**
     * Validate JWT token format (must have 3 parts: header.payload.signature)
     */
    private boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    /**
     * Send RFC 7807 Problem Details error response
     */
    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status,
            String title, String detail, String errorCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        // Create RFC 7807 Problem Detail
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        try {
            // Serialize to JSON using ObjectMapper
            byte[] bytes = objectMapper.writeValueAsBytes(problemDetail);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            // Fallback to simple JSON if serialization fails
            log.error("Failed to serialize error response", e);
            String fallbackJson = String.format(
                    "{\"type\":\"about:blank\",\"title\":\"%s\",\"status\":%d,\"detail\":\"%s\",\"timestamp\":\"%s\",\"errorCode\":\"%s\"}",
                    title, status.value(), detail, Instant.now(), errorCode);
            byte[] bytes = fallbackJson.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
    }
}