package com.intellifix.orchestrator.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;
import java.util.List;

public record SessionMessageDTO(
        Long sessionMsgId,
        Integer seqNum,
        String msgType,
        String msgName,
        Boolean isValid,
        JsonNode rawFixMsg,
        OffsetDateTime dateCreated,
        OffsetDateTime dateModified,
        List<ValidationErrorDTO> validationErrors
) {}
