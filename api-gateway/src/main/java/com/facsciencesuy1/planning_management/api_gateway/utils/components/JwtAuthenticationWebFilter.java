package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
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

        // Skip authentication for public paths
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // No auth header - proceed without authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);

        try {
            String email = jwtService.getEmailFromToken(jwt);

            if (email != null && !jwtService.isTokenExpired(jwt)) {
                return userDetailsService.findByUsername(email)
                        .flatMap(userDetails -> {
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            log.debug("Authenticated user: {} with roles: {}",
                                    email, userDetails.getAuthorities());

                            // FIX: Use contextWrite to set authentication context
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                        })
                        .switchIfEmpty(chain.filter(exchange));
            }
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
        }

        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/ws/");
    }
}