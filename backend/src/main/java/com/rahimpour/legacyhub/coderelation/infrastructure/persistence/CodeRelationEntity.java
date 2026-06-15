package com.rahimpour.legacyhub.coderelation.infrastructure.persistence;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "code_relations")
public class CodeRelationEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "source_symbol_id", nullable = false)
    private UUID sourceSymbolId;

    @Column(name = "target_symbol_id", nullable = false)
    private UUID targetSymbolId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeRelationType type;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CodeRelationEntity() {
        // Required by JPA
    }

    public CodeRelationEntity(
            UUID id,
            UUID projectId,
            UUID sourceSymbolId,
            UUID targetSymbolId,
            CodeRelationType type,
            Instant createdAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.sourceSymbolId = sourceSymbolId;
        this.targetSymbolId = targetSymbolId;
        this.type = type;
        this.createdAt = createdAt;
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
