package com.facsciencesuy1.planning_management.api_gateway.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SwaggerAggregatorController {

    private final WebClient webClient;

    @Value("${services.user-management.url}")
    private String userServiceUrl;

    @Value("${services.academic.url}")
    private String academicServiceUrl;

    @Value("${services.planning.url}")
    private String planningServiceUrl;

    /**
     * Aggregate OpenAPI docs from User Management Service
     */
    @GetMapping("/v3/api-docs/user-management")
    public Mono<ResponseEntity<String>> getUserManagementApiDocs() {
        return fetchApiDocs(userServiceUrl);
    }

    /**
     * Aggregate OpenAPI docs from Academic Service
     */
    @GetMapping("/v3/api-docs/academic")
    public Mono<ResponseEntity<String>> getAcademicServiceApiDocs() {
        return fetchApiDocs(academicServiceUrl);
    }

    /**
     * Aggregate OpenAPI docs from Planning Service
     */
    @GetMapping("/v3/api-docs/planning")
    public Mono<ResponseEntity<String>> getPlanningServiceApiDocs() {
        return fetchApiDocs(planningServiceUrl);
    }

    private Mono<ResponseEntity<String>> fetchApiDocs(String serviceUrl) {
        String apiDocsUrl = serviceUrl + "/v3/api-docs";
        log.debug("Fetching API docs from: {}", apiDocsUrl);

        return webClient
                .get()
                .uri(apiDocsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.debug("Successfully fetched API docs from {}", serviceUrl))
                .doOnError(error -> log.error("Error fetching API docs from {}: {}", serviceUrl, error.getMessage()))
                .onErrorResume(error -> {
                    String fallbackDoc = String.format(
                            "{\"openapi\":\"3.0.1\",\"info\":{\"title\":\"Service Unavailable\",\"description\":\"Unable to fetch API documentation from %s\"},\"paths\":{}}",
                            serviceUrl);
                    return Mono.just(ResponseEntity.ok(fallbackDoc));
                });
    }
}
