package com.rahimpour.legacyhub.codesymbol.infrastructure.persistence;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "code_symbols")
public class CodeSymbolEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "source_file_id", nullable = false)
    private UUID sourceFileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeSymbolType type;

    @Column(nullable = false)
    private String name;

    @Column(name = "fully_qualified_name", columnDefinition = "TEXT")
    private String fullyQualifiedName;

    @Column(name = "start_line")
    private Integer startLine;

    @Column(name = "end_line")
    private Integer endLine;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CodeSymbolEntity() {
        // Required by JPA
    }

    public CodeSymbolEntity(
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
        this.id = id;
        this.projectId = projectId;
        this.sourceFileId = sourceFileId;
        this.type = type;
        this.name = name;
        this.fullyQualifiedName = fullyQualifiedName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.createdAt = createdAt;
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
