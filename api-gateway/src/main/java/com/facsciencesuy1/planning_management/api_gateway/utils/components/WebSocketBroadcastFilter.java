package com.facsciencesuy1.planning_management.api_gateway.utils.components;

import com.facsciencesuy1.planning_management.api_gateway.utils.configs.BrokerWebSocketHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.reactivestreams.Publisher;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketBroadcastFilter implements GlobalFilter, Ordered {
    private final BrokerWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        // Only intercept scheduling endpoints
        if (!path.contains("/api/schedules")) {
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            @NonNull
            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBuffer joinedBuffer = originalResponse.bufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        DataBufferUtils.release(joinedBuffer);

                        String responseBody = new String(content, StandardCharsets.UTF_8);

                        // Only broadcast for successful responses
                        HttpStatusCode statusCode = originalResponse.getStatusCode();
                        if (statusCode != null && statusCode.is2xxSuccessful()) {
                            broadcastToWebSocket(path, method, responseBody);
                        }

                        return originalResponse.bufferFactory().wrap(content);
                    }));
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private void broadcastToWebSocket(String path, HttpMethod method, String responseBody) {
        try {
            Map<String, Object> response = objectMapper.readValue(
                    responseBody,
                    new TypeReference<Map<String, Object>>() {
                    });

            String timetableId = (String) response.get("timetableId");
            if (timetableId == null)
                return;

            String destination = determineDestination(path, method, timetableId);
            if (destination != null) {
                // Use the handler to broadcast
                webSocketHandler.broadcast(destination, response);
                log.info("Broadcasted to WebSocket: {}", destination);
            }
        } catch (Exception e) {
            log.error("Failed to broadcast WebSocket message", e);
        }
    }

    private String determineDestination(String path, HttpMethod method, String timetableId) {
        if (method == null)
            return null;

        String entityType = path.contains("/course") ? "course" : path.contains("/exam") ? "exam" : null;

        if (entityType == null)
            return null;

        String action = null;
        if (method == HttpMethod.POST) {
            action = "create";
        } else if (method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            action = "update";
        } else if (method == HttpMethod.DELETE) {
            action = "delete";
        }

        if (action == null)
            return null;

        return String.format("/topic/timetable/%s/%s/%s", entityType, action, timetableId);
    }

    @Override
    public int getOrder() {
        return -1; // Execute early in the filter chain
    }
}