package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Value("${gateway.secret}")
    private String gatewaySecret;

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    // Store: IP -> (timestamp, count)
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        requestRate(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Add gateway secret header (proves request came from gateway)
            request.setAttribute("X-Gateway-Secret", gatewaySecret);

            // Add user info headers for backend services
            String email = authentication.getName();
            String roles = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.joining(","));

            request.setAttribute("X-User-Email", email);
            request.setAttribute("X-User-Roles", roles);

            log.debug("Added user headers for backend: email={}, roles={}", email, roles);
        }

        filterChain.doFilter(request, response);
    }

    private void requestRate(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        RequestCounter counter = requestCounts.computeIfAbsent(
                clientIp,
                k -> new RequestCounter());

        // Clean up old entries
        cleanupOldEntries();

        // Check rate limit
        if (counter.increment() > requestsPerMinute) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private void cleanupOldEntries() {
        Instant oneMinuteAgo = Instant.now().minus(Duration.ofMinutes(1));
        requestCounts.entrySet().removeIf(entry -> entry.getValue().getTimestamp().isBefore(oneMinuteAgo));
    }

    private static class RequestCounter {
        private Instant timestamp;
        private int count;

        RequestCounter() {
            this.timestamp = Instant.now();
            this.count = 0;
        }

        int increment() {
            Instant oneMinuteAgo = Instant.now().minus(Duration.ofMinutes(1));
            if (timestamp.isBefore(oneMinuteAgo)) {
                // Reset counter after 1 minute
                timestamp = Instant.now();
                count = 1;
            } else {
                count++;
            }
            return count;
        }

        Instant getTimestamp() {
            return timestamp;
        }
    }
}