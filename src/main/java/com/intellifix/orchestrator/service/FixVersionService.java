package com.intellifix.orchestrator.service;

import com.intellifix.orchestrator.entity.FixVersionEntity;
import com.intellifix.orchestrator.mapper.FixVersionMapper;
import com.intellifix.orchestrator.model.FixVersionDTO;
import com.intellifix.orchestrator.repository.FixVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FixVersionService {

    private final FixVersionRepository fixVersionRepository;
    private final FixVersionMapper fixVersionMapper;

    public FixVersionService(FixVersionRepository fixVersionRepository,
            FixVersionMapper fixVersionMapper) {
        this.fixVersionRepository = fixVersionRepository;
        this.fixVersionMapper = fixVersionMapper;
    }

    public FixVersionDTO getFixVersionById(Integer id) {
        log.info("Fetching fix version with ID: {}", id);
        FixVersionEntity entity = fixVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fix version not found with ID: " + id));
        return fixVersionMapper.toDto(entity);
    }

    public List<FixVersionDTO> getAllFixVersions() {
        log.info("Fetching all fix versions");
        List<FixVersionEntity> entities = fixVersionRepository
                .findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
        return entities.stream()
                .map(fixVersionMapper::toDto)
                .collect(Collectors.toList());
    }
}
