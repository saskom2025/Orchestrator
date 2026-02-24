package com.intellifix.orchestrator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "validation_error", schema = "fix_sch")
@Getter
@Setter
public class ValidationErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_msg_id")
    private SessionMessageEntity sessionMessage;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "tag_number")
    private Integer tagNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @Column(name = "date_modified")
    private OffsetDateTime dateModified;
}
