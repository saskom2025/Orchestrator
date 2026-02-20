package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.SimulatorConfigEntity;
import com.intellifix.orchestrator.model.SimulatorConfigDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = FixVersionMapper.class)
public interface SimulatorConfigMapper {

    SimulatorConfigEntity toEntity(SimulatorConfigDTO dto);

    SimulatorConfigDTO toDto(SimulatorConfigEntity entity);
}
