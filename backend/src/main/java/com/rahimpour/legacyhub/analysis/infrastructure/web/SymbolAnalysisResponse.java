package com.rahimpour.legacyhub.analysis.infrastructure.web;

import java.util.UUID;

public record SymbolAnalysisResponse(
        UUID projectId,
        int analyzedFiles,
        int skippedFiles,
        int detectedSymbols
) {
}
