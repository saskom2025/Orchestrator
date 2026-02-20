package com.intellifix.orchestrator.service;

import com.intellifix.orchestrator.entity.SimulatorConfigEntity;
import com.intellifix.orchestrator.mapper.SimulatorConfigMapper;
import com.intellifix.orchestrator.model.SimulatorConfigDTO;
import com.intellifix.orchestrator.repository.SimulatorConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SimulatorConfigService {

    private final SimulatorConfigRepository simulatorConfigRepository;
    private final SimulatorConfigMapper simulatorConfigMapper;

    public SimulatorConfigService(SimulatorConfigRepository simulatorConfigRepository,
            SimulatorConfigMapper simulatorConfigMapper) {
        this.simulatorConfigRepository = simulatorConfigRepository;
        this.simulatorConfigMapper = simulatorConfigMapper;
    }

    public SimulatorConfigDTO createSimulatorConfig(SimulatorConfigDTO simulatorConfigDTO) {
        log.info("Creating new simulator config of type: {}", simulatorConfigDTO.simulatorConfigType());
        SimulatorConfigEntity entity = simulatorConfigMapper.toEntity(simulatorConfigDTO);
        SimulatorConfigEntity savedEntity = simulatorConfigRepository.save(entity);
        return simulatorConfigMapper.toDto(savedEntity);
    }

    public SimulatorConfigDTO getSimulatorConfigById(Integer id) {
        log.info("Fetching simulator config with ID: {}", id);
        SimulatorConfigEntity entity = simulatorConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulator config not found with ID: " + id));
        return simulatorConfigMapper.toDto(entity);
    }

    public List<SimulatorConfigDTO> getAllSimulatorConfigs() {
        log.info("Fetching all simulator configs");
        List<SimulatorConfigEntity> entities = simulatorConfigRepository
                .findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
        return entities.stream()
                .map(simulatorConfigMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SimulatorConfigDTO> getSimulatorConfigsByType(String simulatorConfigType) {
        log.info("Fetching simulator configs by type: {}", simulatorConfigType);
        List<SimulatorConfigEntity> entities = simulatorConfigRepository
                .findBySimulatorConfigTypeOrderByDateCreatedDesc(simulatorConfigType);
        return entities.stream()
                .map(simulatorConfigMapper::toDto)
                .collect(Collectors.toList());
    }
}
