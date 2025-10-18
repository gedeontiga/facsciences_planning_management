package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import org.springframework.beans.factory.annotation.Value;
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
	@Value("${gateway.secret}")
	private String gatewaySecret;

	@Bean
	RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("academic-service", r -> r
						.path("/api/courses/**", "/api/faculties/**", "/api/rooms/**", "/api/ues/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://academic-service"))
				.route("planning-service", r -> r
						.path("/api/schedules/**", "/api/reservations/**", "/api/timetables/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://planning-service"))
				// Separate route for binary file downloads (PDF, CSV exports)
				.route("planning-export-service", r -> r
						.path("/api/export/timetables/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://planning-service"))
				.route("user-management-service", r -> r
						.path("/api/user/**", "/api/admin/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://user-management-service"))
				.route("academic-swagger", r -> r
						.path("/v3/api-docs/academic")
						.filters(f -> f.rewritePath("/v3/api-docs/academic", "/v3/api-docs"))
						.uri("lb://academic-service"))
				.route("planning-swagger", r -> r
						.path("/v3/api-docs/planning")
						.filters(f -> f.rewritePath("/v3/api-docs/planning", "/v3/api-docs"))
						.uri("lb://planning-service"))
				.route("user-management-swagger", r -> r
						.path("/v3/api-docs/user-management")
						.filters(f -> f.rewritePath("/v3/api-docs/user-management", "/v3/api-docs"))
						.uri("lb://user-management-service"))
				.build();
	}

	/**
	 * Adds authentication headers to downstream service requests
	 */
	private GatewayFilter authenticationHeaderFilter() {
		return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
				.flatMap(securityContext -> {
					Authentication auth = securityContext.getAuthentication();

					if (auth != null && auth.isAuthenticated()) {
						String email = auth.getName();
						String roles = auth.getAuthorities().stream()
								.map(authority -> authority.getAuthority().replace("ROLE_", ""))
								.collect(Collectors.joining(","));

						ServerWebExchange mutatedExchange = exchange.mutate()
								.request(builder -> builder
										.header("X-Gateway-Secret", gatewaySecret)
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
	 * Customize codec to handle large binary files
	 */
	@Bean
	CodecCustomizer codecCustomizer() {
		return configurer -> {
			configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10MB
		};
	}
}