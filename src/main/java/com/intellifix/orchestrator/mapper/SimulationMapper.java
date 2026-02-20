package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.SimulationEntity;
import com.intellifix.orchestrator.model.SimulationDTO;
import com.intellifix.orchestrator.model.SimulationRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { SimulatorConfigMapper.class })
public interface SimulationMapper {

    @Mapping(source = "clientSimulator", target = "clientSimulatorConfig")
    @Mapping(source = "brokerSimulator", target = "brokerSimulatorConfig")
    SimulationRequestDTO toDto(SimulationEntity entity);

    @Mapping(source = "clientSimulatorConfig", target = "clientSimulator")
    @Mapping(source = "brokerSimulatorConfig", target = "brokerSimulator")
    SimulationEntity toEntity(SimulationRequestDTO dto);
}