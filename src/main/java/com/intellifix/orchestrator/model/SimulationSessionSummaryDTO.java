package com.intellifix.orchestrator.model;

import java.util.List;

public record SimulationSessionSummaryDTO(

        SimulationDetailDTO simulationDetail,
        List<SimulationSessionObjectDTO> sessionObjects
) {
}
