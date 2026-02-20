package com.intellifix.orchestrator.repository;

import com.intellifix.orchestrator.entity.SimulatorConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulatorConfigRepository extends JpaRepository<SimulatorConfigEntity, Integer> {
    List<SimulatorConfigEntity> findBySimulatorConfigTypeOrderByDateCreatedDesc(String simulatorConfigType);
}
