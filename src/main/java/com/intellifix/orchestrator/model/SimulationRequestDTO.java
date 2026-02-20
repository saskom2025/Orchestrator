package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record SimulationRequestDTO(
        Long simId,

        SimulatorConfigDTO clientSimulatorConfig,

        SimulatorConfigDTO brokerSimulatorConfig,

        FixVersionDTO fixVersion,

        UploadLogDTO uploadLog,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateCreated,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateModified
) {}
