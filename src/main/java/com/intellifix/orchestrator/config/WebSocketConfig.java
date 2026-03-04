package com.intellifix.orchestrator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
     * Connect to ws:http://localhost:8080/intellifix/orchestrator/ws and subscribe to
     * /topic/status/{simId}
     * Simulation Status WS
     * http://localhost:8080/intellifix/orchestrator/ws/topic/status/{simId}
     * FIX Log Message WS
     * http://localhost:8080/intellifix/orchestrator/ws/topic/logs/{simId}
     * Session Heartbeat WS
     * http://localhost:8080/intellifix/orchestrator/ws/topic/health/{simId}/{sessionId}
     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
