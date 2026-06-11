package com.rahimpour.legacyhub.coderelation.infrastructure.web;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;

import java.time.Instant;
import java.util.UUID;

public record CodeRelationResponse(
        UUID id,
        UUID projectId,
        UUID sourceSymbolId,
        UUID targetSymbolId,
        CodeRelationType type,
        Instant createdAt
) {

    public static CodeRelationResponse from(CodeRelation relation) {
        return new CodeRelationResponse(
                relation.getId(),
                relation.getProjectId(),
                relation.getSourceSymbolId(),
                relation.getTargetSymbolId(),
                relation.getType(),
                relation.getCreatedAt()
        );
    }
}
