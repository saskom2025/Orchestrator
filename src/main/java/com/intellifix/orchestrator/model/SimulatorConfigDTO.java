package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record SimulatorConfigDTO(
        Long simulatorConfigId,
        String simulatorConfigName,
        String simulatorConfigType,
        String beginString,
        String senderCompId,
        String targetCompId,
        String socketConnectHost,
        String socketConnectPort,
        FixVersionDTO fixVersion,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateCreated,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateModified
) {}
