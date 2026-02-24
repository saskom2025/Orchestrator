package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "session_message", schema = "fix_sch")
@Getter
@Setter
public class SessionMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionMsgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sim_session_id")
    private SimulationSessionEntity simulationSession;

    @Column(name = "seq_num")
    private Integer seqNum;

    @Column(name = "msg_type", length = 5)
    private String msgType;

    @Column(name = "msg_name", length = 50)
    private String msgName;

    @Column(name = "is_valid")
    private Boolean isValid = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_fix_msg")
    private Map<String, Object> rawFixMsg;

    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @Column(name = "date_modified")
    private OffsetDateTime dateModified;

    @OneToMany(mappedBy = "sessionMessage", cascade = CascadeType.ALL)
    private List<ValidationErrorEntity> validationErrors;
}
