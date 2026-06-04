package com.rahimpour.legacyhub.sourcefile.infrastructure.persistence;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "source_files")
public class SourceFileEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(nullable = false)
    private String filename;

    private String extension;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceFileLanguage language;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "content_hash")
    private String contentHash;

    @Column(name = "storage_key", columnDefinition = "TEXT")
    private String storageKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SourceFileEntity() {
        // Required by JPA
    }

    public SourceFileEntity(
            UUID id,
            UUID projectId,
            String path,
            String filename,
            String extension,
            SourceFileLanguage language,
            long sizeBytes,
            String contentHash,
            String storageKey,
            Instant createdAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.path = path;
        this.filename = filename;
        this.extension = extension;
        this.language = language;
        this.sizeBytes = sizeBytes;
        this.contentHash = contentHash;
        this.storageKey = storageKey;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    public SourceFileLanguage getLanguage() {
        return language;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getContentHash() {
        return contentHash;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
