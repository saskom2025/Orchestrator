package com.intellifix.orchestrator.service;

import com.intellifix.orchestrator.entity.UploadLogEntity;
import com.intellifix.orchestrator.mapper.UploadLogMapper;
import com.intellifix.orchestrator.model.UploadLogDTO;
import com.intellifix.orchestrator.repository.UploadLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UploadLogService {

    @Autowired
    private UploadLogRepository uploadLogRepository;

    @Autowired
    private UploadLogMapper uploadLogMapper;

    public List<UploadLogDTO> getAllUploadSummary(){
        List<UploadLogEntity> allEntities = uploadLogRepository
                .findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));

        return allEntities.stream()
                .map(uploadLogMapper::toDto)
                .collect(Collectors.toList());
    }
}
