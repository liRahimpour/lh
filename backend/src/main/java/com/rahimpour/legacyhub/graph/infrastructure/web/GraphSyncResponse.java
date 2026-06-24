package com.rahimpour.legacyhub.graph.infrastructure.web;

import java.util.UUID;

public record GraphSyncResponse(
        UUID projectId,
        int syncedSymbols
) {
}
