package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record UploadLogDTO(
        Long uploadId,
        String fileName,
        String filePath,
        String uploadStatus,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OffsetDateTime dateCreated
) {}
