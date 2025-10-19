package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

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
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication credentials are required"));
        }

        String jwt = authHeader.substring(7);

        // Validate token format
        if (!isValidTokenFormat(jwt)) {
            log.warn("Invalid token format for path: {}", path);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token format"));
        }

        // Validate token using JwtService
        try {
            if (jwtService.isTokenExpired(jwt)) {
                log.warn("Expired token for path: {}", path);
                return Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Token has expired"));
            }

            if (!jwtService.isTokenValid(jwt)) {
                log.warn("Invalid token for path: {}", path);
                return Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid token"));
            }

            String email = jwtService.getEmailFromToken(jwt);

            if (email == null || email.trim().isEmpty()) {
                log.warn("Token missing email claim for path: {}", path);
                return Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid token: missing email"));
            }

            // Load user details and authenticate BEFORE continuing the chain
            return userDetailsService.findByUsername(email)
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("User not found: {} for path: {}", email, path);
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"));
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
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during authentication for path {}: {}", path, e.getMessage(), e);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Authentication processing failed"));
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/ws/");
    }

    private boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}