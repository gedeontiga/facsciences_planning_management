package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.debug("Processing STOMP CONNECT command");

            // Try to get token from Authorization header
            List<String> authorization = accessor.getNativeHeader("Authorization");
            String jwt = null;

            if (authorization != null && !authorization.isEmpty()) {
                String authHeader = authorization.get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                }
            }

            // Fallback: Try to get token from query parameter (for SockJS compatibility)
            if (jwt == null) {
                List<String> tokenParam = accessor.getNativeHeader("token");
                if (tokenParam != null && !tokenParam.isEmpty()) {
                    jwt = tokenParam.get(0);
                }
            }

            if (jwt != null) {
                try {
                    String username = jwtService.getEmailFromToken(jwt);

                    if (username != null && !jwtService.isTokenExpired(jwt)) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                        // Set authentication in accessor (WebSocket session)
                        accessor.setUser(authToken);

                        log.info("WebSocket authenticated for user: {}", username);
                    } else {
                        log.warn("Invalid or expired JWT token for WebSocket connection");
                        throw new IllegalArgumentException("Invalid or expired token");
                    }
                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage());
                    throw new IllegalArgumentException("Authentication failed", e);
                }
            }
        }

        return message;
    }
}