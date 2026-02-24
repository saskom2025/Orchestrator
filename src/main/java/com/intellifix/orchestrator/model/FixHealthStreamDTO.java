package com.intellifix.orchestrator.model;

import lombok.Builder;

@Builder
public record FixHealthStreamDTO(
        String simId,
        String sessionId,
        String message) {
}
