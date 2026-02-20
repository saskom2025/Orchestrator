package com.intellifix.orchestrator.repository;

import com.intellifix.orchestrator.entity.SimulationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationStatusRepository extends JpaRepository<SimulationStatusEntity, Long> {
}
