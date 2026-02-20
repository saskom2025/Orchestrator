package com.intellifix.orchestrator.redis;

import com.intellifix.orchestrator.model.SimulationStatusDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class StatusStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.status.consumer.group:status_consumer}")
    private String statusConsumerGroup;

    public StatusStreamListener(ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            if(Objects.isNull(message) || Objects.isNull(message.getValue())){
                log.error("Received null message or empty value from status stream");
                return;
            }

            String jsonPayload = message.getValue().get("payload");
            if (StringUtils.isEmpty(jsonPayload)) {
                log.error("Received status message without 'payload' field from stream: {}", message.getStream());
                this.redisTemplate.opsForStream().acknowledge(statusConsumerGroup, message);
                return;
            }

            SimulationStatusDTO statusUpdate = objectMapper.readValue(jsonPayload, SimulationStatusDTO.class);
            if (Objects.isNull(statusUpdate)) {
                log.error("Failed to deserialize SimulationStatusDTO from JSON payload");
                this.redisTemplate.opsForStream().acknowledge(statusConsumerGroup, message);
                return;
            }

            log.info("Broadcasting status update via WebSocket for simId {}: {}", statusUpdate.simId(),
                    statusUpdate.status());

            // Broadcast to a specific topic for this simulation or a general topic
            messagingTemplate.convertAndSend("/topic/status/" + statusUpdate.simId(), statusUpdate);
            messagingTemplate.convertAndSend("/topic/status/all", statusUpdate);

            // Manual Acknowledgment
            this.redisTemplate.opsForStream().acknowledge(statusConsumerGroup, message);
            log.info("Acknowledged message ID: {} in status stream", message.getId());

        } catch (Exception e) {
            log.error("Error processing status update from Redis Stream: {}", e.getMessage());
        }
    }
}
