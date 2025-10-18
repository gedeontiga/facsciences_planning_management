package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ReactiveWebSocketAuthHandler implements WebSocketHandler {
    private final WebSocketHandler delegate;
    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        String jwt = extractToken(session);

        if (jwt == null) {
            log.warn("No JWT token for WebSocket session {}", session.getId());
            return session.close();
        }

        try {
            String email = jwtService.getEmailFromToken(jwt);

            if (email == null || jwtService.isTokenExpired(jwt)) {
                log.warn("Invalid JWT for WebSocket session {}", session.getId());
                return session.close();
            }

            return userDetailsService.findByUsername(email)
                    .flatMap(userDetails -> {
                        var auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        session.getAttributes().put("userEmail", email);
                        log.info("WebSocket authenticated: {} (session: {})", email, session.getId());

                        return delegate.handle(session)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                    })
                    .switchIfEmpty(session.close());
        } catch (Exception e) {
            log.error("WebSocket auth failed: {}", e.getMessage());
            return session.close();
        }
    }

    private String extractToken(WebSocketSession session) {
        // Try Authorization header
        String authHeader = session.getHandshakeInfo().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Try query parameter
        String query = session.getHandshakeInfo().getUri().getQuery();
        if (query != null && query.contains("token=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }
        return null;
    }
}