package com.intellifix.orchestrator.model;

import java.time.OffsetDateTime;

public record ValidationErrorDTO(
        Long errorId,
        String errorCode,
        Integer tagNumber,
        String description,
        OffsetDateTime dateCreated,
        OffsetDateTime dateModified
) {}
