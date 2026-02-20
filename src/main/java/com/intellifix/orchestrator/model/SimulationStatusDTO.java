package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.time.OffsetDateTime;

@Builder
public record SimulationStatusDTO(
        Long simId,
        String status,
        String message,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime timestamp
) {}
