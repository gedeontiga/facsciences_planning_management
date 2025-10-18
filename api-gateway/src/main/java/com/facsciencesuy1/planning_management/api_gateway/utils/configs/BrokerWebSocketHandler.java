package com.facsciencesuy1.planning_management.api_gateway.utils.configs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BrokerWebSocketHandler implements WebSocketHandler {
    private final Map<String, Map<String, WebSocketSession>> subscriptions = new ConcurrentHashMap<>();
    private final Sinks.Many<BroadcastMessage> messageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        String userEmail = (String) session.getAttributes().get("userEmail");
        log.info("WebSocket connected: {} (user: {})", session.getId(), userEmail);

        Mono<Void> input = session.receive()
                .doOnNext(msg -> handleMessage(session, msg.getPayloadAsText()))
                .then();

        Flux<String> output = messageSink.asFlux()
                .filter(msg -> isSubscribed(session.getId(), msg.destination))
                .map(msg -> serialize(msg));

        return session.send(output.map(session::textMessage))
                .and(input)
                .doFinally(signal -> {
                    log.info("WebSocket closed: {} (user: {})", session.getId(), userEmail);
                    removeSession(session.getId());
                });
    }

    private void handleMessage(WebSocketSession session, String payload) {
        try {
            Map<String, String> msg = objectMapper.readValue(payload, new TypeReference<>() {
            });
            String cmd = msg.get("command");
            String dest = msg.get("destination");

            if ("SUBSCRIBE".equals(cmd) && dest != null) {
                subscriptions.computeIfAbsent(dest, k -> new ConcurrentHashMap<>())
                        .put(session.getId(), session);
                log.debug("Subscribed: {} to {}", session.getId(), dest);
            } else if ("UNSUBSCRIBE".equals(cmd) && dest != null) {
                Map<String, WebSocketSession> sessions = subscriptions.get(dest);
                if (sessions != null)
                    sessions.remove(session.getId());
                log.debug("Unsubscribed: {} from {}", session.getId(), dest);
            }
        } catch (Exception e) {
            log.error("Invalid message from {}: {}", session.getId(), e.getMessage());
        }
    }

    private boolean isSubscribed(String sessionId, String destination) {
        Map<String, WebSocketSession> sessions = subscriptions.get(destination);
        return sessions != null && sessions.containsKey(sessionId);
    }

    private void removeSession(String sessionId) {
        subscriptions.values().forEach(sessions -> sessions.remove(sessionId));
    }

    private String serialize(BroadcastMessage msg) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "destination", msg.destination,
                    "data", msg.payload,
                    "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("Serialization error: {}", e.getMessage());
            return "{}";
        }
    }

    public void broadcast(String destination, Object payload) {
        messageSink.tryEmitNext(new BroadcastMessage(destination, payload));
    }

    private record BroadcastMessage(String destination, Object payload) {
    }
}