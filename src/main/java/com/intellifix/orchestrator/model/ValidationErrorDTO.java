package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ValidationErrorDTO(
        Long errorId,
        String errorCode,
        String tags,
        String description,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateCreated,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateModified
) {}
