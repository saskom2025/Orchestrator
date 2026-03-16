package com.intellifix.orchestrator.redis;

import com.intellifix.orchestrator.config.FixMessageComparisonConfig;
import com.intellifix.orchestrator.entity.SessionMessageEntity;
import com.intellifix.orchestrator.entity.SimulationEntity;
import com.intellifix.orchestrator.entity.SimulationSessionEntity;
import com.intellifix.orchestrator.entity.ValidationErrorEntity;
import com.intellifix.orchestrator.mapper.SimulationSessionMapper;
import com.intellifix.orchestrator.model.FixLogStreamDTO;
import com.intellifix.orchestrator.model.ValidationErrorDTO;
import com.intellifix.orchestrator.redis.strategy.FixMessageStrategy;
import com.intellifix.orchestrator.repository.SessionMessageRepository;
import com.intellifix.orchestrator.repository.SimulationRepository;
import com.intellifix.orchestrator.repository.SimulationSessionRepository;
import com.intellifix.orchestrator.utils.IntellifixUtils;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Service
public class FixLogStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimulationRepository simulationRepository;
    private final SimulationSessionRepository simulationSessionRepository;
    private final SessionMessageRepository sessionMessageRepository;
    private final FixMessageComparisonConfig fixMessageComparisonConfig;
    private final Map<String, FixMessageStrategy> strategies;
    private final ObjectMapper objectMapper;
    private final SimulationSessionMapper simulationSessionMapper;

    @Value("${redis.log.consumer.group:log_consumer}")
    private String logConsumerGroup;

    public FixLogStreamListener(SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate,
            SimulationRepository simulationRepository,
            SimulationSessionRepository simulationSessionRepository,
            SessionMessageRepository sessionMessageRepository,
            FixMessageComparisonConfig fixMessageComparisonConfig,
            Map<String, FixMessageStrategy> strategies,
            ObjectMapper objectMapper,
            SimulationSessionMapper simulationSessionMapper) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.simulationRepository = simulationRepository;
        this.simulationSessionRepository = simulationSessionRepository;
        this.sessionMessageRepository = sessionMessageRepository;
        this.fixMessageComparisonConfig = fixMessageComparisonConfig;
        this.strategies = strategies;
        this.objectMapper = objectMapper;
        this.simulationSessionMapper = simulationSessionMapper;
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
            String messageDescription = values.get("messageDescription");
            String simIdStr = values.get("simId");
            String sessionId = values.get("sessionId");
            String fixMessage = values.get("message");
            String error = values.get("error");

            if (StringUtils.isEmpty(simIdStr) || StringUtils.isEmpty(sessionId) || StringUtils.isEmpty(fixMessage)) {
                log.error("Missing required fields in log stream message: {}", values);
                acknowledge(message);
                return;
            }

            // parse simIdStr
            Long simId = IntellifixUtils.extractSimId(simIdStr);
            if (Objects.isNull(simId)) {
                log.error("Error parsing simId from: {}", simIdStr);
                acknowledge(message);
                return;
            }
            // Enrich Log Message Stream to persist
            ValidationErrorDTO validationErrorDTO = null;
            try {
                if (StringUtils.isNotEmpty(error)) {
                    validationErrorDTO = objectMapper.readValue(error, ValidationErrorDTO.class);
                }
            } catch (Exception e) {
                log.error("Error parsing validation error JSON: {}", error, e);
            }

            FixLogStreamDTO logMessage = FixLogStreamDTO.builder()
                    .simId(simIdStr)
                    .sessionId(sessionId)
                    .messageType(messageType)
                    .messageDescription(messageDescription)
                    .message(fixMessage)
                    .error(validationErrorDTO)
                    .build();
            // Fetch Simulation entity from DB
            SimulationEntity simulationEntity = simulationRepository.findById(simId)
                    .orElseThrow(() -> new RuntimeException("Simulation not found for ID: " + simId));

            // Extract SenderCompID (tag 49) from FIX message
            Map<String, Object> parsedFix = IntellifixUtils.parseFixMessage(fixMessage);
            String senderCompId = (String) parsedFix.get("49");
            String targetCompId = simulationEntity.getClientSimulator().getTargetCompId();

            if (isMessageEligibleForAiAnalysis(messageType, senderCompId, targetCompId)) {
                log.info("Processing message for AI: type={}, sender={}", messageType, targetCompId);
                // call to AI logic here
            }

            // persist to db tables to save state
            persistMessage(simulationEntity, simId, sessionId, logMessage);

            // publish to WebSocket /topic/logs/simIdStr
            // http://localhost:8080/intellifix/orchestrator/ws/topic/logs/{simId}
            log.info("Broadcasting FIX log message for simulation {}: {}", simIdStr, messageType);
            messagingTemplate.convertAndSend("/topic/logs/" + simIdStr, logMessage);
            // messagingTemplate.convertAndSend("/topic/logs/all", logMessage);

            // Manual Acknowledgment
            acknowledge(message);
            log.info("Acknowledged message ID: {} in log stream", message.getId());

        } catch (Exception e) {
            log.error("Error processing FIX log update from Redis Stream: {}", e.getMessage(), e);
        }
    }

    private boolean isMessageEligibleForAiAnalysis(String messageType, String senderCompId, String targetCompId) {
        List<String> eligibleTypes = fixMessageComparisonConfig.getMessageTypes();
        if (eligibleTypes == null || !eligibleTypes.contains(messageType)) {
            return false;
        }
        // Differennt message startegies can be extended for custom filter logic
        return Optional.ofNullable(strategies.get(messageType))
                .map(strategy -> strategy.isEligible(senderCompId, targetCompId))
                .orElse(false);
    }

    private void persistMessage(SimulationEntity simulationEntity, Long simId, String sessionId, FixLogStreamDTO dto) {

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
        msgEntity.setMsgName(dto.messageDescription());
        msgEntity.setRawFixMsg(IntellifixUtils.parseFixMessage(dto.message()));
        msgEntity.setDateCreated(OffsetDateTime.now());
        msgEntity.setDateModified(OffsetDateTime.now());

        // Extract sequence number tag 34
        msgEntity.setSeqNum(IntellifixUtils.extractSeqNum(dto.message()));

        if (dto.error() != null) {
            ValidationErrorEntity errorEntity = simulationSessionMapper.toValidationErrorEntity(dto.error());
            errorEntity.setSessionMessage(msgEntity);
            errorEntity.setDateCreated(OffsetDateTime.now());
            errorEntity.setDateModified(OffsetDateTime.now());
            msgEntity.setValidationErrors(Collections.singletonList(errorEntity));
            msgEntity.setIsValid(false);
        }

        sessionMessageRepository.save(msgEntity);
    }

    private void acknowledge(MapRecord<String, String, String> message) {
        this.redisTemplate.opsForStream().acknowledge(logConsumerGroup, message);
    }
}
