package com.intellifix.orchestrator.model;

import lombok.Builder;

@Builder
public record FixLogStreamDTO(
        String messageType,
        String simId,
        String sessionId,
        String message) {
}
