package com.rahimpour.legacyhub.graph.domain;

import java.util.List;
import java.util.UUID;

public record GraphTraversalResult(
        UUID centerSymbolId,
        GraphTraversalDirection direction,
        int requestedDepth,
        int resultCount,
        List<GraphTraversalItem> results
) {
}
