package com.intellifix.orchestrator.model;

import java.time.OffsetDateTime;
import java.util.List;

public record SimulationSessionDTO(
        Long simSessionId,
        String fixSessionId,
        Long simId,
        String status,
        OffsetDateTime dateCreated,
        OffsetDateTime dateModified,
        List<SessionMessageDTO> messages
) {}
