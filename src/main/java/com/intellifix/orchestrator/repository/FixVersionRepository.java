package com.intellifix.orchestrator.repository;

import com.intellifix.orchestrator.entity.FixVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixVersionRepository extends JpaRepository<FixVersionEntity, Integer> {
}
