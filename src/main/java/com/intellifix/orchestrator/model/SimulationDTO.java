package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record SimulationDTO(
        Long simId,
        @JsonIgnore
        String simulatorConfig,
        @NotBlank
        String fixVersion,
        @NotBlank
        String logPath,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateCreated,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateModified
) {}
