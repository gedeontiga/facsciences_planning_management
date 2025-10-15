package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtFilter;
import com.facsciencesuy1.planning_management.dtos.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	@Value("${cors.allowed-origins}")
	private String allowedOrigins;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(authorize -> authorize
						// Public endpoints
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/actuator/health").permitAll()

						// Swagger/OpenAPI endpoints
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/v3/api-docs/**").permitAll()
						.requestMatchers("/api-docs/**").permitAll()

						// WebSocket endpoints (validated in WebSocket interceptor)
						.requestMatchers("/ws/**").permitAll()

						// Actuator endpoints
						.requestMatchers("/actuator/**").permitAll()

						// Admin endpoints
						.requestMatchers("/api/admin/**").hasAuthority("ADMIN")

						// All other endpoints require authentication
						.anyRequest().authenticated())

				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(customAuthEntryPoint())
						.accessDeniedHandler(customAccessDeniedHandler()))

				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

				.logout(logout -> logout
						.logoutUrl("/api/auth/logout")
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies("JSESSIONID")
						.logoutSuccessHandler((request, response, authentication) -> {
							response.setStatus(HttpServletResponse.SC_OK);
							response.setContentType("application/json");
							response.getWriter().write("{\"message\":\"Logout successful\"}");
						}))

				.build();
	}

	@Bean
	AuthenticationEntryPoint customAuthEntryPoint() {
		return (request, response, authException) -> {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			String message = authException.getMessage() != null
					? authException.getMessage()
					: "Full authentication is required to access this resource";

			if (authException.getCause() instanceof JwtException) {
				message = "Invalid JWT token: " + authException.getCause().getMessage();
			}

			ErrorResponse errorResponse = new ErrorResponse(
					"Authentication Error",
					message,
					HttpStatus.UNAUTHORIZED.value(),
					LocalDateTime.now().toString());

			ObjectMapper mapper = new ObjectMapper();
			response.getWriter().write(mapper.writeValueAsString(errorResponse));
		};
	}

	@Bean
	AccessDeniedHandler customAccessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);

			ErrorResponse errorResponse = new ErrorResponse(
					"Access Denied",
					"You don't have permission to access this resource",
					HttpStatus.FORBIDDEN.value(),
					LocalDateTime.now().toString());

			ObjectMapper mapper = new ObjectMapper();
			response.getWriter().write(mapper.writeValueAsString(errorResponse));
		};
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Parse allowed origins from properties
		String[] origins = allowedOrigins.split(",");
		configuration.setAllowedOriginPatterns(Arrays.asList(origins));

		configuration.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));

		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList(
				"Authorization",
				"Content-Type",
				"X-Total-Count"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Apply to all endpoints
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}