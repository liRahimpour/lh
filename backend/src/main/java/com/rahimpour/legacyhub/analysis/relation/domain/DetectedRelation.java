package com.rahimpour.legacyhub.analysis.relation.domain;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;

import java.util.UUID;

public record DetectedRelation(
        UUID sourceSymbolId,
        UUID targetSymbolId,
        CodeRelationType type
) {
} // Das ist wie bei DetectedSymbol
