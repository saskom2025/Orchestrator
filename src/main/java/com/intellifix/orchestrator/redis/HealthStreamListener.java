package com.intellifix.orchestrator.redis;

import com.intellifix.orchestrator.model.FixHealthStreamDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class HealthStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.health.consumer.group:health_consumer}")
    private String healthConsumerGroup;

    public HealthStreamListener(SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            if (Objects.isNull(message) || Objects.isNull(message.getValue())) {
                log.error("Received null message or empty value from health stream");
                return;
            }

            // Read fields directly from the MapRecord instead of a nested JSON payload
            String simId = message.getValue().get("simId");
            String sessionId = message.getValue().get("sessionId");
            String messageText = message.getValue().get("message");

            if (simId == null && sessionId == null) {
                log.error("Received health message without required fields (simId/sessionId) from stream: {}",
                        message.getStream());
                this.redisTemplate.opsForStream().acknowledge(healthConsumerGroup, message);
                return;
            }

            FixHealthStreamDTO healthUpdate = new FixHealthStreamDTO(simId, sessionId, messageText);

            log.info("Broadcasting FIX heartbeat via WebSocket for simId {}: {}", healthUpdate.simId(),
                    healthUpdate.sessionId());

            // Broadcast to a specific topic for this simulation heartbeat
            messagingTemplate.convertAndSend("/topic/health/" + healthUpdate.simId(), healthUpdate);
            messagingTemplate.convertAndSend("/topic/health/all", healthUpdate);

            // Manual Acknowledgment
            this.redisTemplate.opsForStream().acknowledge(healthConsumerGroup, message);
            log.info("Acknowledged message ID: {} in health stream", message.getId());

        } catch (Exception e) {
            log.error("Error processing health update from Redis Stream: {}", e.getMessage());
        }
    }
}
