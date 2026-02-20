package com.intellifix.orchestrator.service;

import com.intellifix.orchestrator.entity.SimulationEntity;
import com.intellifix.orchestrator.entity.SimulatorConfigEntity;
import com.intellifix.orchestrator.mapper.SimulationMapper;
import com.intellifix.orchestrator.mapper.StatusMapper;
import com.intellifix.orchestrator.model.*;
import com.intellifix.orchestrator.repository.SimulationRepository;
import com.intellifix.orchestrator.repository.SimulationStatusRepository;
import com.intellifix.orchestrator.entity.SimulationStatusEntity;
import com.intellifix.orchestrator.redis.RedisStreamProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellifix.orchestrator.utils.SimulationStatus;
import com.intellifix.orchestrator.utils.SimulationStatusMessages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final SimulationStatusRepository statusRepository;
    private final SimulationMapper simulationMapper;
    private final StatusMapper statusMapper;
    private final RedisStreamProducer redisStreamProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public SimulationService(SimulationRepository simulationRepository, SimulationStatusRepository statusRepository,
            SimulationMapper simulationMapper,
            StatusMapper statusMapper, RedisStreamProducer redisStreamProducer,
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {

        this.simulationRepository = simulationRepository;
        this.statusRepository = statusRepository;
        this.simulationMapper = simulationMapper;
        this.statusMapper = statusMapper;
        this.redisStreamProducer = redisStreamProducer;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SimulationRequestDTO createSimulation(SimulationRequestDTO simulationDTO) {

        SimulationEntity simulationEntityToSave = simulationMapper.toEntity(simulationDTO);

        // Persist initial status into DB via cascading save
        SimulationStatusEntity statusEntity = new SimulationStatusEntity();
        statusEntity.setSimulation(simulationEntityToSave);
        statusEntity.setStatus(SimulationStatus.IN_PROGRESS.name());
        statusEntity.setMessage(SimulationStatusMessages.getMessage(SimulationStatus.IN_PROGRESS.name()));

        // Set as current status to ensure sim_status_id is populated
        simulationEntityToSave.setCurrentStatus(statusEntity);

        SimulationEntity savedSimulationEntity = simulationRepository.save(simulationEntityToSave);
        SimulationRequestDTO savedSimulationDTO = simulationMapper.toDto(savedSimulationEntity);
        SimulationStatusDTO savedStatusDTO = statusMapper.toDto(savedSimulationEntity.getCurrentStatus());
        ClientSimulatorConfigDTO clientSimulatorConfigDTO = getClientSimulatorConfig(savedSimulationEntity);
        BrokerSimulatorConfigDTO brokerSimulatorConfigDTO = getBrokerSimulatorConfig(savedSimulationEntity);
        // Publish to Redis Stream after successful DB entry

        redisStreamProducer.publishBrokerSimulation(brokerSimulatorConfigDTO);
        redisStreamProducer.publishClientSimulation(clientSimulatorConfigDTO);

        redisStreamProducer.publishSimulation(savedSimulationDTO);
        redisStreamProducer.publishStatus(savedStatusDTO);
        return savedSimulationDTO;
    }

    private ClientSimulatorConfigDTO getClientSimulatorConfig(SimulationEntity entity){
        ClientSimulatorConfigDTO clientConfig = null;
        if(Objects.nonNull(entity)){
            SimulatorConfigEntity simConfig = entity.getClientSimulator();
            clientConfig = ClientSimulatorConfigDTO.builder()
                    .simId(entity.getSimId())
                    .beginString(simConfig.getBeginString())
                    .senderCompId(simConfig.getSenderCompId())
                    .targetCompId(simConfig.getTargetCompId())
                    .socketConnectHost(simConfig.getSocketConnectHost())
                    .socketConnectPort(simConfig.getSocketConnectPort())
                    .dataDictionary(entity.getFixVersion().getFixVersionName())
                    .build();
        }
        return clientConfig;
    }

    private BrokerSimulatorConfigDTO getBrokerSimulatorConfig(SimulationEntity entity){
        BrokerSimulatorConfigDTO brokerConfig = null;
        if(Objects.nonNull(entity)){
            SimulatorConfigEntity simConfig = entity.getBrokerSimulator();
            brokerConfig = BrokerSimulatorConfigDTO.builder()
                    .simId(entity.getSimId())
                    .beginString(simConfig.getBeginString())
                    .senderCompId(simConfig.getSenderCompId())
                    .targetCompId(simConfig.getTargetCompId())
                    .socketConnectPort(simConfig.getSocketConnectPort())
                    .dataDictionary(entity.getFixVersion().getFixVersionName())
                    .build();
        }
        return brokerConfig;
    }

    @Transactional
    public void updateStatus(SimulationStatusDTO requestDTO) {
        log.info("Updating status for simulation ID: {}", requestDTO.simId());

        SimulationEntity simulationEntity = simulationRepository.findById(requestDTO.simId())
                .orElseThrow(() -> new RuntimeException("Simulation not found with ID: " + requestDTO.simId()));

        SimulationStatusEntity statusEntity = simulationEntity.getCurrentStatus();
        if (statusEntity == null) {
            statusEntity = new SimulationStatusEntity();
            statusEntity.setSimulation(simulationEntity);
            simulationEntity.setCurrentStatus(statusEntity);
        }

        statusEntity.setStatus(requestDTO.status());
        String status = requestDTO.status();
        statusEntity.setMessage(switch (status) {
            case String s when SimulationStatus.COMPLETED.name().equalsIgnoreCase(s) ->
                SimulationStatusMessages.getMessage(SimulationStatus.COMPLETED.name());
            case String s when SimulationStatus.FAILED.name().equalsIgnoreCase(s) ->
                SimulationStatusMessages.getMessage(SimulationStatus.FAILED.name());
            case String s when SimulationStatus.CANCELLED.name().equalsIgnoreCase(s) ->
                SimulationStatusMessages.getMessage(SimulationStatus.CANCELLED.name());
            default -> StringUtils.isNotBlank(requestDTO.message()) ? requestDTO.message() : statusEntity.getMessage();
        });

        SimulationStatusEntity savedStatusEntity = statusRepository.save(statusEntity);
        SimulationStatusDTO savedStatusDTO = statusMapper.toDto(savedStatusEntity);
        redisStreamProducer.publishStatus(savedStatusDTO);
    }

    public List<SimulationRequestDTO> getAllSimulations() {

        List<SimulationEntity> simulationEntities = simulationRepository
                .findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateCreated"))).getContent();
        return simulationEntities.stream()
                .map(sim -> simulationMapper.toDto(sim))
                .collect(Collectors.toList());
    }

}
