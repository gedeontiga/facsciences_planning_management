package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced // Important: Active le support du load balancer
    WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
