package com.rahimpour.legacyhub.coderelation.infrastructure.web;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;

import java.time.Instant;
import java.util.UUID;

public record EnrichedCodeRelationResponse(
        UUID id,
        UUID projectId,

        UUID sourceSymbolId,
        String sourceSymbolName,
        CodeSymbolType sourceSymbolType,

        UUID targetSymbolId,
        String targetSymbolName,
        CodeSymbolType targetSymbolType,

        CodeRelationType type,
        Instant createdAt
) {

    public static EnrichedCodeRelationResponse from(
            CodeRelation relation,
            CodeSymbol sourceSymbol,
            CodeSymbol targetSymbol
    ) {
        return new EnrichedCodeRelationResponse(
                relation.getId(),
                relation.getProjectId(),

                relation.getSourceSymbolId(),
                sourceSymbol.getName(),
                sourceSymbol.getType(),

                relation.getTargetSymbolId(),
                targetSymbol.getName(),
                targetSymbol.getType(),

                relation.getType(),
                relation.getCreatedAt()
        );
    }
}
