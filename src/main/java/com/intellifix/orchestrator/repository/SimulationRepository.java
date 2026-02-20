package com.intellifix.orchestrator.repository;

import com.intellifix.orchestrator.entity.SimulationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends JpaRepository<SimulationEntity, Long> {
}
