package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "simulation_status", schema = "fix_sch")
@Getter
@Setter
public class SimulationStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sim_status_id")
    private Long simStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sim_id", nullable = false)
    private SimulationEntity simulation;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private OffsetDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "date_modified")
    private OffsetDateTime dateModified;
}
