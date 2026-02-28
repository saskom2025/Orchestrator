package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.SessionMessageEntity;
import com.intellifix.orchestrator.entity.SimulationEntity;
import com.intellifix.orchestrator.entity.SimulationSessionEntity;
import com.intellifix.orchestrator.entity.ValidationErrorEntity;
import com.intellifix.orchestrator.model.SessionMessageDTO;
import com.intellifix.orchestrator.model.SimulationDetailDTO;
import com.intellifix.orchestrator.model.SimulationSessionDetailDTO;
import com.intellifix.orchestrator.model.ValidationErrorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { FixVersionMapper.class, UploadLogMapper.class, StatusMapper.class })
public interface SimulationSessionMapper {

    @Mapping(source = "simulation", target = "simulationDetail")
    SimulationSessionDetailDTO toDetailDto(SimulationSessionEntity entity);

    SimulationDetailDTO toSimulationDetailDto(SimulationEntity entity);

    SessionMessageDTO toMessageDto(SessionMessageEntity entity);

    ValidationErrorDTO toValidationErrorDto(ValidationErrorEntity entity);

    List<SimulationSessionDetailDTO> toDetailDtoList(List<SimulationSessionEntity> entities);

    default com.fasterxml.jackson.databind.JsonNode map(java.util.Map<String, Object> value) {
        if (value == null) {
            return null;
        }
        return new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(value);
    }
}
