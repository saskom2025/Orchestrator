package com.intellifix.orchestrator.repository;

import com.intellifix.orchestrator.entity.UploadLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadLogRepository extends JpaRepository<UploadLogEntity, Long> {

}
