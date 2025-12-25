package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import com.facsciencesuy1.planning_management.api_gateway.utils.components.JwtAuthenticationWebFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

	@Value("${cors.allowed-origins}")
	private String allowedOrigins;

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
				.logout(ServerHttpSecurity.LogoutSpec::disable)
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/api/auth/**").permitAll()
						.pathMatchers("/actuator/health").permitAll()
						.pathMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
						.pathMatchers("/v3/api-docs/**", "/api-docs/**").permitAll()
						.pathMatchers("/webjars/**").permitAll()
						.pathMatchers("/ws/**").permitAll()
						.pathMatchers("/api/admin/**").hasAuthority("ADMIN")
						.anyExchange().authenticated())
				.exceptionHandling(exception -> exception
						// Fix: Delegate to a helper method for type safety
						.authenticationEntryPoint((exchange, ex) -> writeErrorResponse(exchange,
								HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage()))
						.accessDeniedHandler((exchange, ex) -> writeErrorResponse(exchange, HttpStatus.FORBIDDEN,
								"Forbidden", "Access Denied")))
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String title, String detail) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

		String requestPath = exchange.getRequest().getPath().value();
		String safeDetail = detail == null ? "" : detail.replace("\"", "'");

		String body = String.format(
				"{\"type\":\"about:blank\",\"title\":\"%s\",\"status\":%d,\"detail\":\"%s\",\"instance\":\"%s\"}",
				title, status.value(), safeDetail, requestPath);

		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = response.bufferFactory().wrap(bytes);

		return response.writeWith(Mono.just(buffer));
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		String[] origins = allowedOrigins.split(",");
		configuration.setAllowedOriginPatterns(Arrays.asList(origins));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}