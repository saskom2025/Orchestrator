package com.intellifix.orchestrator.redis;

import com.intellifix.orchestrator.model.SimulationDTO;
import com.intellifix.orchestrator.model.SimulationStatusDTO;
import com.intellifix.orchestrator.service.SimulationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Service
public class RedisStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.stream.name:fix_stream}")
    private String streamName;

    @Value("${redis.simulation.consumer.group:fix_consumer}")
    private String consumerGroup;

    public RedisStreamConsumer(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        /*try {
            if (Objects.isNull(message) || Objects.isNull(message.getValue())) {
                log.error("Received null message or empty value from stream");
                return;
            }

            String jsonPayload = message.getValue().get("payload");
            if (StringUtils.isEmpty(jsonPayload)) {
                log.error("Received message without 'payload' field from stream: {}", message.getStream());
                this.redisTemplate.opsForStream().acknowledge(consumerGroup, message);
                return;
            }

            SimulationDTO dto = objectMapper.readValue(jsonPayload, SimulationDTO.class);
            if (Objects.isNull(dto)) {
                log.error("Failed to deserialize SimulationDTO from JSON payload");
                this.redisTemplate.opsForStream().acknowledge(consumerGroup, message);
                return;
            }

            log.info("RECEIVED FROM STREAM [{}]: {}", message.getStream(), dto);

            // Manual Acknowledgment
            this.redisTemplate.opsForStream().acknowledge(consumerGroup, message);
            log.info("Acknowledged message ID: {} in stream: {}", message.getId(), streamName);

        } catch (Exception e) {
            log.error("Error processing message from stream: {}. Error type: {}", streamName, e.getClass().getName());
            log.error("Full exception details: ", e);
        }*/
    }
}
