package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import com.facsciencesuy1.planning_management.api_gateway.services.JwtService;
import com.facsciencesuy1.planning_management.api_gateway.utils.components.ReactiveWebSocketAuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ReactiveWebSocketConfig {
    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Bean
    BrokerWebSocketHandler brokerWebSocketHandler() {
        return new BrokerWebSocketHandler();
    }

    @Bean
    HandlerMapping webSocketHandlerMapping(BrokerWebSocketHandler brokerHandler) {
        WebSocketHandler authenticatedHandler = new ReactiveWebSocketAuthHandler(
                brokerHandler, jwtService, userDetailsService);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1);
        mapping.setUrlMap(Map.of("/ws", authenticatedHandler));
        return mapping;
    }

    @Bean
    WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}