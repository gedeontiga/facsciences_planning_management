package com.facsciencesuy1.planning_management.user_management_service.utils.components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Value("${gateway.secret}")
    private String expectedGatewaySecret;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Allow public endpoints
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String gatewaySecret = request.getHeader("X-Gateway-Secret");

        // Verify request comes from gateway
        if (gatewaySecret == null || !gatewaySecret.equals(expectedGatewaySecret)) {
            log.warn("Unauthorized direct access attempt from IP: {} to path: {}",
                    getClientIp(request), requestPath);
            sendJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden", "Direct service access not allowed. Use API Gateway.");
            return;
        }

        String email = request.getHeader("X-User-Email");
        String roles = request.getHeader("X-User-Roles");

        // Set authentication context if headers present
        if (email != null && roles != null) {
            List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());

            UserDetails userDetails = new User(email, "N/A", authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user: {} with authorities: {}", email, authorities);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        return ip != null && !ip.isEmpty() ? ip : request.getRemoteAddr();
    }

    private void sendJsonError(HttpServletResponse response, int status,
            String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"statusCode\":%d,\"timestamp\":\"%s\"}",
                error, message, status, java.time.LocalDateTime.now().toString()));
    }
}