package com.intellifix.orchestrator.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellifix.orchestrator.entity.SessionMessageEntity;
import com.intellifix.orchestrator.entity.SimulationEntity;
import com.intellifix.orchestrator.entity.SimulationSessionEntity;
import com.intellifix.orchestrator.model.FixLogStreamDTO;
import com.intellifix.orchestrator.repository.SessionMessageRepository;
import com.intellifix.orchestrator.repository.SimulationRepository;
import com.intellifix.orchestrator.repository.SimulationSessionRepository;
import com.intellifix.orchestrator.utils.IntellifixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FixLogStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimulationRepository simulationRepository;
    private final SimulationSessionRepository simulationSessionRepository;
    private final SessionMessageRepository sessionMessageRepository;

    @Value("${redis.log.consumer.group:log_consumer}")
    private String logConsumerGroup;

    public FixLogStreamListener(SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate,
            SimulationRepository simulationRepository,
            SimulationSessionRepository simulationSessionRepository,
            SessionMessageRepository sessionMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.simulationRepository = simulationRepository;
        this.simulationSessionRepository = simulationSessionRepository;
        this.sessionMessageRepository = sessionMessageRepository;
    }

    @Override
    @Transactional
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            if (Objects.isNull(message) || Objects.isNull(message.getValue())) {
                log.error("Received null message or empty value from log stream");
                return;
            }

            Map<String, String> values = message.getValue();
            String messageType = values.get("messageType");
            String simIdStr = values.get("simId");
            String sessionId = values.get("sessionId");
            String fixMessage = values.get("message");

            if (simIdStr == null || sessionId == null || fixMessage == null) {
                log.error("Missing required fields in log stream message: {}", values);
                acknowledge(message);
                return;
            }

            // parse simIdStr
            Long simId = IntellifixUtils.extractSimId(simIdStr);
            if (simId == null) {
                log.error("Error parsing simId from: {}", simIdStr);
                acknowledge(message);
                return;
            }

            FixLogStreamDTO logUpdate = FixLogStreamDTO.builder()
                    .messageType(messageType)
                    .simId(simIdStr)
                    .sessionId(sessionId)
                    .message(fixMessage)
                    .build();

            // persist to db tables to save state
            persistMessage(simId, sessionId, logUpdate);

            // publish to WebSocket /topic/logs/simIdStr
            log.info("Broadcasting FIX log message for simulation {}: {}", simIdStr, messageType);
            messagingTemplate.convertAndSend("/topic/logs/" + simIdStr, logUpdate);
            messagingTemplate.convertAndSend("/topic/logs/all", logUpdate);

            // Manual Acknowledgment
            acknowledge(message);
            log.info("Acknowledged message ID: {} in log stream", message.getId());

        } catch (Exception e) {
            log.error("Error processing FIX log update from Redis Stream: {}", e.getMessage(), e);
        }
    }

    private void persistMessage(Long simId, String sessionId, FixLogStreamDTO dto) {
        SimulationEntity simulationEntity = simulationRepository.findById(simId)
                .orElseThrow(() -> new RuntimeException("Simulation not found for ID: " + simId));

        SimulationSessionEntity sessionEntity = simulationSessionRepository
                .findByFixSessionIdAndSimulationSimId(sessionId, simId)
                .orElseGet(() -> {
                    log.info("Creating new SimulationSessionEntity for sessionId: {} and simId: {}", sessionId, simId);
                    SimulationSessionEntity newSession = new SimulationSessionEntity();
                    newSession.setFixSessionId(sessionId);
                    newSession.setSimulation(simulationEntity);
                    newSession.setStatus("ACTIVE");
                    newSession.setDateCreated(OffsetDateTime.now());
                    newSession.setDateModified(OffsetDateTime.now());
                    return simulationSessionRepository.save(newSession);
                });

        SessionMessageEntity msgEntity = new SessionMessageEntity();
        msgEntity.setSimulationSession(sessionEntity);
        msgEntity.setMsgType(dto.messageType());
        msgEntity.setRawFixMsg(IntellifixUtils.parseFixMessage(dto.message()));
        msgEntity.setDateCreated(OffsetDateTime.now());
        msgEntity.setDateModified(OffsetDateTime.now());

        // Extract sequence number tag 34
        msgEntity.setSeqNum(IntellifixUtils.extractSeqNum(dto.message()));

        sessionMessageRepository.save(msgEntity);
    }

    private void acknowledge(MapRecord<String, String, String> message) {
        this.redisTemplate.opsForStream().acknowledge(logConsumerGroup, message);
    }
}
