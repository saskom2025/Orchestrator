package com.intellifix.orchestrator.model;

import lombok.Builder;

@Builder
public record BrokerSimulatorConfigDTO(

        Long simId,
        String brokerSimConfigName,
        String beginString,
        String senderCompId,
        String targetCompId,
        String socketConnectPort,
        String dataDictionary
) {}
