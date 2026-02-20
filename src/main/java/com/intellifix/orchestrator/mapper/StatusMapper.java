package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.SimulationStatusEntity;
import com.intellifix.orchestrator.model.SimulationStatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    @Mapping(target = "simId", source = "simulation.simId")
    @Mapping(target = "timestamp", source = "createdDate")
    SimulationStatusDTO toDto(SimulationStatusEntity entity);

    @Mapping(target = "simulation.simId", source = "simId")
    @Mapping(target = "createdDate", source = "timestamp")
    @Mapping(target = "simStatusId", ignore = true)
    SimulationStatusEntity toEntity(SimulationStatusDTO dto);
}
