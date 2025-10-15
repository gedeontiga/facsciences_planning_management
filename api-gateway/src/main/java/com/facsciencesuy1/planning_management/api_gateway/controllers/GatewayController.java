package com.facsciencesuy1.planning_management.api_gateway.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Enumeration;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GatewayController {

	private final WebClient webClient;

	@Value("${services.user-management.url}")
	private String userServiceUrl;

	@Value("${services.academic.url}")
	private String academicServiceUrl;

	@Value("${services.planning.url}")
	private String planningServiceUrl;

	@Value("${gateway.secret}")
	private String gatewaySecret;

	@RequestMapping(value = "/api/rooms/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "academic-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "academic-service")
	public Mono<ResponseEntity<String>> proxyUsers(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(academicServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/faculties/**", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "academic-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "academic-service")
	public Mono<ResponseEntity<String>> proxyDepartments(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(academicServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/courses/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "academic-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "academic-service")
	public Mono<ResponseEntity<String>> proxyCourses(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(academicServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/ues/**", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "academic-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "academic-service")
	public Mono<ResponseEntity<String>> proxyNotifications(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(academicServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/schedules/**", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "planning-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "planning-service")
	public Mono<ResponseEntity<String>> proxySchedules(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(planningServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/reservations/**", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "planning-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "planning-service")
	public Mono<ResponseEntity<String>> proxyReservations(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(planningServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/export/timetables/**", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "planning-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "planning-service")
	public Mono<ResponseEntity<String>> proxyExportTimetables(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(planningServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/timetables/**", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "planning-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "planning-service")
	public Mono<ResponseEntity<String>> proxyTimetables(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return forwardRequest(planningServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/admin/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "user-management-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "user-management-service")
	public Mono<ResponseEntity<String>> proxyAdmin(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		// Route to user-management for now
		return forwardRequest(userServiceUrl, request, body);
	}

	@RequestMapping(value = "/api/user/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.PATCH })
	@CircuitBreaker(name = "user-management-service", fallbackMethod = "fallbackResponse")
	@Retry(name = "user-management-service")
	public Mono<ResponseEntity<String>> proxyUser(
			HttpServletRequest request,
			@RequestBody(required = false) String body) {
		// Route to user-management for now
		return forwardRequest(userServiceUrl, request, body);
	}

	private Mono<ResponseEntity<String>> forwardRequest(
			String baseUrl,
			HttpServletRequest request,
			String body) {

		String path = request.getRequestURI();
		String queryString = request.getQueryString();
		String targetUrl = baseUrl + path + (queryString != null ? "?" + queryString : "");

		log.debug("Forwarding {} {} to {}", request.getMethod(), path, targetUrl);

		return webClient
				.method(HttpMethod.valueOf(request.getMethod()))
				.uri(targetUrl)
				.headers(headers -> copyHeaders(request, headers))
				.bodyValue(body != null ? body : "")
				.retrieve()
				.toEntity(String.class)
				.doOnSuccess(response -> log.debug("Successfully forwarded to {}", targetUrl))
				.doOnError(error -> log.error("Error forwarding to {}: {}", targetUrl,
						error.getMessage()));
	}

	private void copyHeaders(HttpServletRequest request, HttpHeaders headers) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (!shouldSkipHeader(headerName)) {
				headers.add(headerName, request.getHeader(headerName));
			}
		}

		// Add gateway authentication headers
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()) {
			// Gateway secret (proves request came from gateway)
			headers.add("X-Gateway-Secret", gatewaySecret);

			// User information
			headers.add("X-User-Email", auth.getName());

			String roles = auth.getAuthorities().stream()
					.map(a -> a.getAuthority().replace("ROLE_", ""))
					.collect(Collectors.joining(","));
			headers.add("X-User-Roles", roles);

			log.debug("Added authentication headers for user: {}", auth.getName());
		}
	}

	private boolean shouldSkipHeader(String headerName) {
		String lowerName = headerName.toLowerCase();
		return lowerName.equals("host") ||
				lowerName.equals("content-length") ||
				lowerName.equals("connection") ||
				lowerName.startsWith("x-forwarded");
	}

	Mono<ResponseEntity<String>> fallbackResponse(
			HttpServletRequest request,
			String body,
			Exception ex) {

		log.error("Circuit breaker triggered for {} {}: {}",
				request.getMethod(), request.getRequestURI(), ex.getMessage());

		String errorResponse = String.format(
				"{\"error\":\"Service temporarily unavailable\",\"path\":\"%s\",\"timestamp\":\"%s\",\"details\":\"%s\"}",
				request.getRequestURI(),
				java.time.Instant.now(),
				ex.getMessage());

		return Mono.just(ResponseEntity
				.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorResponse));
	}
}
