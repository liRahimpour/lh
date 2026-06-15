package com.rahimpour.legacyhub.analysis.relation.infrastructure.web;

import java.util.UUID;

public record RelationAnalysisResponse(
        UUID projectId,
        int analyzedFiles,
        int skippedFiles,
        int detectedRelations
) {
}
