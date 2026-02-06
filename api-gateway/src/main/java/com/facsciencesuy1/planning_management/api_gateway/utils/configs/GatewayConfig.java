package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import java.util.stream.Collectors;

@Configuration
public class GatewayConfig {

	@Bean
	RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				// Academic Service Routes
				.route("academic-service", r -> r
						.path("/api/courses/**", "/api/faculties/**", "/api/rooms/**", "/api/ues/**")
						.filters(f -> f
								.filter(authenticationHeaderFilter())
								.retry(retryConfig -> retryConfig.setRetries(2)))
						.uri("lb://academic-service"))

				// Planning Service Routes
				.route("planning-service", r -> r
						.path("/api/schedules/**", "/api/reservations/**", "/api/timetables/**")
						.filters(f -> f
								.filter(authenticationHeaderFilter())
								.retry(retryConfig -> retryConfig.setRetries(2)))
						.uri("lb://planning-service"))

				// Planning Export Service Routes
				.route("planning-export-service", r -> r
						.path("/api/export/timetables/**")
						.filters(f -> f
								.filter(authenticationHeaderFilter())
								.retry(retryConfig -> retryConfig.setRetries(2)))
						.uri("lb://planning-service"))

				// User Service Routes
				.route("user-service", r -> r
						.path("/api/user/**", "/api/admin/**")
						.filters(f -> f
								.filter(authenticationHeaderFilter())
								.retry(retryConfig -> retryConfig.setRetries(2)))
						.uri("lb://user-service"))

				// Swagger Documentation Routes for each service
				.route("academic-swagger", r -> r
						.path("/v3/api-docs/academic")
						.filters(f -> f.rewritePath("/v3/api-docs/academic", "/v3/api-docs"))
						.uri("lb://academic-service"))

				.route("planning-swagger", r -> r
						.path("/v3/api-docs/planning")
						.filters(f -> f.rewritePath("/v3/api-docs/planning", "/v3/api-docs"))
						.uri("lb://planning-service"))

				.route("user-swagger", r -> r
						.path("/v3/api-docs/user")
						.filters(f -> f.rewritePath("/v3/api-docs/user", "/v3/api-docs"))
						.uri("lb://user-service"))

				.build();
	}

	private GatewayFilter authenticationHeaderFilter() {
		return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
				.flatMap(securityContext -> {
					Authentication auth = securityContext.getAuthentication();

					if (auth != null && auth.isAuthenticated()) {
						String email = auth.getName();
						String roles = auth.getAuthorities().stream()
								.map(authority -> {
									String role = authority.getAuthority();
									return role.startsWith("ROLE_") ? role.substring(5) : role;
								})
								.collect(Collectors.joining(","));

						// REMOVE X-Gateway-Secret header
						ServerWebExchange mutatedExchange = exchange.mutate()
								.request(builder -> builder
										.header("X-User-Email", email)
										.header("X-User-Roles", roles))
								.build();

						return chain.filter(mutatedExchange);
					}

					return chain.filter(exchange);
				})
				.switchIfEmpty(chain.filter(exchange));
	}

	/**
	 * Customize codec to handle large binary files (PDFs, Excel, images, etc.)
	 * Increases buffer size to 10MB to prevent DataBufferLimitException
	 */
	@Bean
	CodecCustomizer codecCustomizer() {
		return configurer -> {
			// 10MB buffer for large files
			configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024);
			// Enable streaming mode for large responses
			configurer.defaultCodecs().enableLoggingRequestDetails(false);
		};
	}
}