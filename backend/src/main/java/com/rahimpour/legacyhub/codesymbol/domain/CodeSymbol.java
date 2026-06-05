package com.rahimpour.legacyhub.codesymbol.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CodeSymbol {

    private final UUID id;
    private final UUID projectId;
    private final UUID sourceFileId;
    private final CodeSymbolType type;
    private final String name;
    private final String fullyQualifiedName;
    private final Integer startLine;
    private final Integer endLine;
    private final Instant createdAt;

    public CodeSymbol(
            UUID id,
            UUID projectId,
            UUID sourceFileId,
            CodeSymbolType type,
            String name,
            String fullyQualifiedName,
            Integer startLine,
            Integer endLine,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.sourceFileId = Objects.requireNonNull(sourceFileId, "sourceFileId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.name = validateName(name);
        this.fullyQualifiedName = fullyQualifiedName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static CodeSymbol create(
            UUID projectId,
            UUID sourceFileId,
            CodeSymbolType type,
            String name,
            String fullyQualifiedName,
            Integer startLine,
            Integer endLine
    ) {
        return new CodeSymbol(
                UUID.randomUUID(),
                projectId,
                sourceFileId,
                type,
                name,
                fullyQualifiedName,
                startLine,
                endLine,
                Instant.now()
        );
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Code symbol name must not be empty");
        }
        return name.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getSourceFileId() {
        return sourceFileId;
    }

    public CodeSymbolType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
