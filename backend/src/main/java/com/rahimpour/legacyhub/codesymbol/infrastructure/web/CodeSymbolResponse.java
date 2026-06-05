package com.rahimpour.legacyhub.codesymbol.infrastructure.web;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;

import java.time.Instant;
import java.util.UUID;

public record CodeSymbolResponse(
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

    public static CodeSymbolResponse from(CodeSymbol symbol) {
        return new CodeSymbolResponse(
                symbol.getId(),
                symbol.getProjectId(),
                symbol.getSourceFileId(),
                symbol.getType(),
                symbol.getName(),
                symbol.getFullyQualifiedName(),
                symbol.getStartLine(),
                symbol.getEndLine(),
                symbol.getCreatedAt()
        );
    }
}
