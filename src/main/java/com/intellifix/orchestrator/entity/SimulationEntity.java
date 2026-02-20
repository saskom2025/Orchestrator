package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "simulation", schema = "fix_sch")
@Getter
@Setter
public class SimulationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sim_id")
    private Long simId;

    @ManyToOne
    @JoinColumn(name = "client_sim_id", referencedColumnName = "simulator_config_id")
    private SimulatorConfigEntity clientSimulator;

    @ManyToOne
    @JoinColumn(name = "broker_sim_id", referencedColumnName = "simulator_config_id")
    private SimulatorConfigEntity brokerSimulator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fix_version_id")
    private FixVersionEntity fixVersion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_id")
    private UploadLogEntity uploadLog;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sim_status_id", referencedColumnName = "sim_status_id")
    private SimulationStatusEntity currentStatus;

    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulationStatusEntity> statusHistory;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_modified")
    private OffsetDateTime dateModified;

}