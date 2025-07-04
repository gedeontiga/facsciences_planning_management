package com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.configs;

import java.time.LocalDateTime;
import java.util.Arrays;

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

import com.facsciences_planning_management.facsciences_planning_management.dto.ErrorResponse;
import com.facsciences_planning_management.facsciences_planning_management.user_auth_service.managers.services.JwtFilter;
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

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/admin/**").hasAuthority("ADMIN")
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/actuator/**").permitAll()
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
			String message = authException.getMessage() != null ? authException.getMessage()
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

		// Updated CORS configuration for production
		configuration.setAllowedOriginPatterns(Arrays.asList(
				"https://facsciences-planning-management.netlify.app",
				"https://facsciences-uy1-planning-management-gedeontiga-eabfb5d3.koyeb.app",
				"https://app-planning-uy1-web.vercel.app",
				"http://localhost:*",
				"https://localhost:*"));

		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

		// Important: Add preflight request handling
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		// Also register for Swagger UI paths
		source.registerCorsConfiguration("/swagger-ui/**", configuration);
		source.registerCorsConfiguration("/v3/api-docs/**", configuration);

		return source;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}