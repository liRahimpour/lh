package com.rahimpour.legacyhub.analysis.domain;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;

public record DetectedSymbol(
        CodeSymbolType type,
        String name,
        String fullyQualifiedName,
        Integer startLine,
        Integer endLine
) {
}
