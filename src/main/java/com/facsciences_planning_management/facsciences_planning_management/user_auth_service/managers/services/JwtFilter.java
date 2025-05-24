package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.models.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response, @NonNull final FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip JWT processing for preflight requests
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        Boolean isTokenExpired = true;
        String email = null;
        Jwt jwtLoaded = new Jwt();
        try {
            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                final String token = authorization.substring(7);
                jwtLoaded = jwtService.getJwtByToken(token);
                isTokenExpired = jwtService.isTokenExpired(token);
                email = jwtService.getEmailFromToken(token);
            }

            if (!isTokenExpired
                    && jwtLoaded.getUser().getEmail().equals(email)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails userDetails = userService.loadUserByUsername(email);
                final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
