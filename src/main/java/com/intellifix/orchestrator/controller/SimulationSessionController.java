package com.intellifix.orchestrator.controller;

import com.intellifix.orchestrator.model.SimulationSessionDetailDTO;
import com.intellifix.orchestrator.model.SimulationSessionObjectDTO;
import com.intellifix.orchestrator.model.SimulationSessionSummaryDTO;
import com.intellifix.orchestrator.service.SimulationSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/simulation/session")
@RequiredArgsConstructor
public class SimulationSessionController {

    private final SimulationSessionService simulationSessionService;

    @GetMapping
    public List<SimulationSessionDetailDTO> getAllSessionDetail() {
        return simulationSessionService.getAllSessionDetail();
    }

    @GetMapping("/{sessionId}")
    public SimulationSessionDetailDTO getSessionDetailBySessionID(@PathVariable("id") Long sessionID) {
        return simulationSessionService.getSessionDetailBySessionID(sessionID);
    }

    // TODO create an endpoint to get session details by simulation id
    @GetMapping("/simulation/{id}")
    public List<SimulationSessionDetailDTO> getSessionDetailBySimulationID(@PathVariable("id") Long simulationID) {
        return simulationSessionService.getSessionDetailBySimulationID(simulationID);
    }

    @GetMapping("/summary/{simId}")
    public SimulationSessionSummaryDTO getSessionSummary(@PathVariable("simId") Long simulationID) {
        return simulationSessionService.getSessionSummaryBySimulationId(simulationID);
    }
}
