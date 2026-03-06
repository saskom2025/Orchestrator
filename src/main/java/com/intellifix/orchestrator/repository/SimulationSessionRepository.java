package com.intellifix.orchestrator.repository;

import com.intellifix.orchestrator.entity.SimulationSessionEntity;
import com.intellifix.orchestrator.model.SimulationSessionObjectDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationSessionRepository extends JpaRepository<SimulationSessionEntity, Long> {
    Optional<SimulationSessionEntity> findByFixSessionIdAndSimulationSimId(String fixSessionId, Long simId);

    List<SimulationSessionEntity> findBySimulationSimId(Long simId);

    @Query("SELECT new com.intellifix.orchestrator.model.SimulationSessionObjectDTO(" +
            "CAST(s.simSessionId AS string), " +
            "s.fixSessionId, " +
            "CASE WHEN LOCATE(':', s.fixSessionId) > 0 " +
            "THEN SUBSTRING(s.fixSessionId, LOCATE(':', s.fixSessionId) + 1) " +
            "ELSE s.fixSessionId END, s.status) " +
            "FROM SimulationSessionEntity s WHERE s.simulation.simId = :simId")
    List<SimulationSessionObjectDTO> findSessionObjectsBySimulationId(Long simId);
}
