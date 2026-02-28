package com.intellifix.orchestrator.model;

import lombok.Builder;

@Builder
public record FixLogStreamDTO(

        String simId,
        String sessionId,
        String messageType,
        String message) {
}
