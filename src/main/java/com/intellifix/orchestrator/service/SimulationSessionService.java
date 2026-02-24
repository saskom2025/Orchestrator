package com.intellifix.orchestrator.service;

import com.intellifix.orchestrator.entity.SimulationSessionEntity;
import com.intellifix.orchestrator.mapper.SimulationSessionMapper;
import com.intellifix.orchestrator.model.SimulationSessionDetailDTO;
import com.intellifix.orchestrator.repository.SimulationSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationSessionService {

    private final SimulationSessionRepository simulationSessionRepository;
    private final SimulationSessionMapper simulationSessionMapper;

    @Transactional(readOnly = true)
    public List<SimulationSessionDetailDTO> getAllSessionDetail() {
        log.info("Fetching all simulation sessions");
        List<SimulationSessionEntity> sessions = simulationSessionRepository
                .findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
        return simulationSessionMapper.toDetailDtoList(sessions);
    }

    @Transactional(readOnly = true)
    public SimulationSessionDetailDTO getSessionDetailBySessionID(Long sessionID) {
        if (sessionID == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        log.info("Fetching session details for ID: {}", sessionID);
        SimulationSessionEntity session = simulationSessionRepository.findById(sessionID)
                .orElseThrow(() -> new RuntimeException("Simulation session not found with ID: " + sessionID));
        return simulationSessionMapper.toDetailDto(session);
    }

    @Transactional(readOnly = true)
    public List<SimulationSessionDetailDTO> getSessionDetailBySimulationID(Long simulationID) {
        if (simulationID == null) {
            throw new IllegalArgumentException("Simulation ID cannot be null");
        }
        log.info("Fetching session details for simulation ID: {}", simulationID);
        List<SimulationSessionEntity> sessions = simulationSessionRepository.findBySimulationSimId(simulationID);
        return simulationSessionMapper.toDetailDtoList(sessions);
    }

}
