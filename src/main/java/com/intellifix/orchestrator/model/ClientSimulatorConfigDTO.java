package com.intellifix.orchestrator.model;

import lombok.Builder;

@Builder
public record ClientSimulatorConfigDTO(

        Long simId,

        String clientSimConfigName,
        String beginString,
        String senderCompId,
        String targetCompId,
        String socketConnectHost,
        String socketConnectPort,
        String dataDictionary

) {}
