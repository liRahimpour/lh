package com.rahimpour.legacyhub.coderelation.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CodeRelation {

    private final UUID id;
    private final UUID projectId;
    private final UUID sourceSymbolId;
    private final UUID targetSymbolId;
    private final CodeRelationType type;
    private final Instant createdAt;

    public CodeRelation(
            UUID id,
            UUID projectId,
            UUID sourceSymbolId,
            UUID targetSymbolId,
            CodeRelationType type,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.sourceSymbolId = Objects.requireNonNull(sourceSymbolId, "sourceSymbolId must not be null");
        this.targetSymbolId = Objects.requireNonNull(targetSymbolId, "targetSymbolId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");

        if (sourceSymbolId.equals(targetSymbolId)) {
            throw new IllegalArgumentException("A code relation cannot point to itself");
        }
    }

    public static CodeRelation create(
            UUID projectId,
            UUID sourceSymbolId,
            UUID targetSymbolId,
            CodeRelationType type
    ) {
        return new CodeRelation(
                UUID.randomUUID(),
                projectId,
                sourceSymbolId,
                targetSymbolId,
                type,
                Instant.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getSourceSymbolId() {
        return sourceSymbolId;
    }

    public UUID getTargetSymbolId() {
        return targetSymbolId;
    }

    public CodeRelationType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
