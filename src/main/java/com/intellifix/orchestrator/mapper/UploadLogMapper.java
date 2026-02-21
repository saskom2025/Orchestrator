package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.UploadLogEntity;
import com.intellifix.orchestrator.model.UploadLogDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UploadLogMapper {

    UploadLogDTO toDto(UploadLogEntity entity);

    UploadLogEntity toEntity(UploadLogDTO dto);
}
