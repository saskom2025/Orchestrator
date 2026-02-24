package com.intellifix.orchestrator.model;

import java.time.OffsetDateTime;
import java.util.List;

public record SimulationSessionDetailDTO(
        Long simSessionId,
        String fixSessionId,
        SimulationDetailDTO simulationDetail,
        String status,
        OffsetDateTime dateCreated,
        OffsetDateTime dateModified,
        List<SessionMessageDTO> messages
) {}
