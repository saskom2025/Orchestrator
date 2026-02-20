package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "fix_version", schema = "fix_sch")
@Getter
@Setter
public class FixVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fix_version_id")
    private Long fixVersionId;

    @Column(name = "fix_version_name", nullable = false)
    private String fixVersionName;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_modified")
    private OffsetDateTime dateModified;

    // One record can have many simulator configurations
    @OneToMany(mappedBy = "fixVersion", cascade = CascadeType.ALL)
    private List<SimulatorConfigEntity> simulatorConfigs;
}
