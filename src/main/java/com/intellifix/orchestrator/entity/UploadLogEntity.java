package com.intellifix.orchestrator.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "upload_log", schema = "fix_sch")
@Getter
@Setter
public class UploadLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_id")
    private Long uploadId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "upload_status")
    private String uploadStatus;

    @Column(name = "date_created", updatable = false)
    private OffsetDateTime dateCreated;

    @Column(name = "date_modified")
    private OffsetDateTime dateModified;
}
