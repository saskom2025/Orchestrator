package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.SessionMessageEntity;
import com.intellifix.orchestrator.entity.SimulationEntity;
import com.intellifix.orchestrator.entity.SimulationSessionEntity;
import com.intellifix.orchestrator.entity.ValidationErrorEntity;
import com.intellifix.orchestrator.model.SessionMessageDTO;
import com.intellifix.orchestrator.model.SimulationDetailDTO;
import com.intellifix.orchestrator.model.SimulationSessionDetailDTO;
import com.intellifix.orchestrator.model.SimulationSessionObjectDTO;
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

    ValidationErrorEntity toValidationErrorEntity(ValidationErrorDTO dto);

    List<SimulationSessionDetailDTO> toDetailDtoList(List<SimulationSessionEntity> entities);

    @Mapping(source = "simSessionId", target = "sessionId")
    @Mapping(source = "fixSessionId", target = "sessionName")
    @Mapping(target = "connectionText", expression = "java(mapConnectionText(entity))")
    SimulationSessionObjectDTO toSessionListDto(SimulationSessionEntity entity);

    List<SimulationSessionObjectDTO> toSessionListDtoList(List<SimulationSessionEntity> entities);

    default String mapConnectionText(SimulationSessionEntity entity) {
        if (entity == null || entity.getFixSessionId() == null) {
            return null;
        }
        String fixSessionId = entity.getFixSessionId();
        int colonIndex = fixSessionId.indexOf(':');
        if (colonIndex != -1 && colonIndex < fixSessionId.length() - 1) {
            return fixSessionId.substring(colonIndex + 1);
        }
        return fixSessionId;
    }

    default com.fasterxml.jackson.databind.JsonNode map(java.util.Map<String, Object> value) {
        if (value == null) {
            return null;
        }
        return new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(value);
    }
}
