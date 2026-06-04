package com.rahimpour.legacyhub.sourcefile.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


public class SourceFile {

    private final UUID id;
    private final UUID projectId;
    private final String path;
    private final String filename;
    private final String extension;
    private final SourceFileLanguage language;
    private final long sizeBytes;
    private final String contentHash;
    private final String storageKey;
    private final Instant createdAt;

    public SourceFile(
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
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.path = validateRequired(path, "path");
        this.filename = validateRequired(filename, "filename");
        this.extension = extension;
        this.language = Objects.requireNonNull(language, "language must not be null");
        this.sizeBytes = validateSize(sizeBytes);
        this.contentHash = contentHash;
        this.storageKey = storageKey;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static SourceFile create(
            UUID projectId,
            String path,
            String filename,
            String extension,
            SourceFileLanguage language,
            long sizeBytes,
            String contentHash,
            String storageKey
    ) {
        return new SourceFile(
                UUID.randomUUID(),
                projectId,
                path,
                filename,
                extension,
                language,
                sizeBytes,
                contentHash,
                storageKey,
                Instant.now()
        );
    }

    private static String validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        return value.trim();
    }

    private static long validateSize(long sizeBytes) {
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("sizeBytes must not be negative");
        }
        return sizeBytes;
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
