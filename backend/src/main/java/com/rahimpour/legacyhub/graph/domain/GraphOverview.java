package com.rahimpour.legacyhub.graph.domain;

import java.util.Map;
import java.util.UUID;

public record GraphOverview(
        UUID projectId,
        long symbolCount,
        long relationCount,
        Map<String, Long> relationTypeCounts
) {
}

// cool für späteren Dashboard:
//{
//        "projectId": "...",
//        "symbolCount": 42,
//        "relationCount": 67,
//        "relationTypeCounts": {
//        "HAS_METHOD": 20,
//        "HAS_FIELD": 13,
//        "CALLS": 15,
//        "USES": 19
//        }
//        }