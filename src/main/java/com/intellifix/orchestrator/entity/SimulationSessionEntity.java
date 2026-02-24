package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "simulation_session", schema = "fix_sch")
@Getter
@Setter
public class SimulationSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simSessionId;

    @Column(name = "fix_session_id")
    private String fixSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sim_id")
    private SimulationEntity simulation;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @Column(name = "date_modified")
    private OffsetDateTime dateModified;

    @OneToMany(mappedBy = "simulationSession", cascade = CascadeType.ALL)
    private List<SessionMessageEntity> messages;
}
