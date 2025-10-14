package com.facsciencesuy1.planning_management.api_gateway.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.facsciencesuy1.planning_management.api_gateway.components.JwtChannelInterceptor;

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