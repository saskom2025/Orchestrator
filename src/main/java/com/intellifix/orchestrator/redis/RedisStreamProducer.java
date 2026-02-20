package com.intellifix.orchestrator.redis;

import com.intellifix.orchestrator.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;

@Slf4j
@Service
public class RedisStreamProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.simulation.stream.name:sim_stream}")
    private String simulationStreamName;

    @Value("${redis.simulation.client.stream.name:sim_client_stream}")
    private String simulationClientStreamName;

    @Value("${redis.simulation.broker.stream.name:sim_broker_stream}")
    private String simulationBrokerStreamName;

    @Value("${redis.status.stream.name:status_stream}")
    private String statusStreamName;

    public RedisStreamProducer(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSimulation(SimulationRequestDTO dto) {

        try {
            log.info("Attempting to publish DTO to stream {}: {}", simulationStreamName, dto);

            // Manual JSON serialization for maximum reliability with OffsetDateTime and
            // Records
            String jsonPayload = objectMapper.writeValueAsString(dto);

            MapRecord<String, String, String> record = StreamRecords.newRecord()
                    .in(simulationStreamName)
                    .ofMap(Collections.singletonMap("payload", jsonPayload));

            this.redisTemplate.opsForStream().add(record);
            log.info("Successfully published to stream: {} ID: {}", simulationStreamName, dto.simId());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to publish to redis stream: {}. Error: {}", simulationStreamName, e.getMessage());
            log.error("Full stack trace: ", e);
        }
    }

    public void publishClientSimulation(ClientSimulatorConfigDTO dto){

        try {
            log.info("Attempting to publish DTO to stream {}: {}", simulationClientStreamName, dto);
            String jsonPayload = objectMapper.writeValueAsString(dto);
            MapRecord<String, String, String> record = StreamRecords.newRecord()
                    .in(simulationClientStreamName)
                    .ofMap(Collections.singletonMap("payload", jsonPayload));

            this.redisTemplate.opsForStream().add(record);
            log.info("Successfully published to stream: {} ID: {}", simulationClientStreamName, dto.simId());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to publish to redis stream: {}. Error: {}", simulationClientStreamName, e.getMessage());
            log.error("Full stack trace: ", e);
        }
    }

    public void publishBrokerSimulation(BrokerSimulatorConfigDTO dto){

        try {
            log.info("Attempting to publish DTO to stream {}: {}", simulationBrokerStreamName, dto);
            String jsonPayload = objectMapper.writeValueAsString(dto);
            MapRecord<String, String, String> record = StreamRecords.newRecord()
                    .in(simulationBrokerStreamName)
                    .ofMap(Collections.singletonMap("payload", jsonPayload));

            this.redisTemplate.opsForStream().add(record);
            log.info("Successfully published to stream: {} ID: {}", simulationBrokerStreamName, dto.simId());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to publish to redis stream: {}. Error: {}", simulationBrokerStreamName, e.getMessage());
            log.error("Full stack trace: ", e);
        }
    }

    public void publishStatus(SimulationStatusDTO statusUpdate) {
        try {
            String jsonStatus = objectMapper.writeValueAsString(statusUpdate);

            MapRecord<String, String, String> record = StreamRecords
                    .newRecord()
                    .in(statusStreamName)
                    .ofMap(Collections.singletonMap("payload", jsonStatus));

            this.redisTemplate.opsForStream().add(record);
            log.info("Published status update to Redis: simulation {}: {}", statusUpdate.simId(),
                    statusUpdate.status());
        } catch (Exception e) {
            log.error("Failed to publish status update for simulation {}: {}", statusUpdate.simId(), e.getMessage());
        }
    }
}
