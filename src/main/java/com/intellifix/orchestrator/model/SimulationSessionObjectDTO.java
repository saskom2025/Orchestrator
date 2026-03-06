package com.intellifix.orchestrator.model;

public record SimulationSessionObjectDTO(
        String sessionId,
        String sessionName,
        String connectionText,

        String status
) {}
