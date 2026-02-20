package com.intellifix.orchestrator.mapper;

import com.intellifix.orchestrator.entity.FixVersionEntity;
import com.intellifix.orchestrator.model.FixVersionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FixVersionMapper {

    FixVersionEntity toEntity(FixVersionDTO dto);

    FixVersionDTO toDto(FixVersionEntity entity);
}
