package com.facsciencesuy1.planning_management.api_gateway.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        // This is the modern replacement for the deprecated configureInbound method.
        // It secures the message destinations.

        messages
                // Any client can subscribe to destinations starting with /topic/
                // This is for public broadcasts (like our reservation updates).
                .simpDestMatchers("/topic/**").permitAll()

                // Messages sent to destinations starting with /app/ require the user to be
                // authenticated.
                // This is for actions initiated by the user, e.g., sending a private message.
                .simpDestMatchers("/app/**").authenticated()

                // All other messages (not matching the above) are denied.
                // It's a good practice to have a default-deny policy.
                .anyMessage().denyAll();

        return messages.build();
    }
}