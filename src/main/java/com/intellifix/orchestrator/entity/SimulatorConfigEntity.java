package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
@Entity
@Table(name = "simulator_config", schema = "fix_sch")
@Getter
@Setter
public class SimulatorConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "simulator_config_id")
    private Long simulatorConfigId;

    @Column(name = "simulator_config_name")
    private String simulatorConfigName;

    @Column(name = "simulator_config_type")
    private String simulatorConfigType;

    @Column(name = "begin_string")
    private String beginString;

    @Column(name = "sender_comp_id")
    private String senderCompId;

    @Column(name = "target_comp_id")
    private String targetCompId;

    @Column(name = "socket_connect_host")
    private String socketConnectHost;

    @Column(name = "socket_connect_port")
    private String socketConnectPort;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_modified")
    private OffsetDateTime dateModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fix_version_id")
    private FixVersionEntity fixVersion;
}
