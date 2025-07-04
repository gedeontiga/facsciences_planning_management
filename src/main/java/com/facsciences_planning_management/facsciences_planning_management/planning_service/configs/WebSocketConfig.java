package com.facsciences_planning_management.facsciences_planning_management.planning_service.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.components.JwtChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Use setAllowedOriginPatterns for flexible origin matching
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "https://facsciences-uy1-planning-management-gedeontiga-eabfb5d3.koyeb.app",
                        "http://facsciences-uy1-planning-management-gedeontiga-eabfb5d3.koyeb.app",
                        "https://app-planning-uy1-web.vercel.app",
                        "http://localhost:*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}