package com.rahimpour.legacyhub.graph.domain;

import java.util.UUID;

public record GraphSymbol(
        UUID id,
        UUID projectId,
        UUID sourceFileId,
        String name,
        String type,
        String fullyQualifiedName,
        Integer startLine,
        Integer endLine
) {
}

//TODO:es enthält fast dieselben Daten wie CodeSymbol (src/main/java/com/rahimpour/legacyhub/codesymbol/domain/CodeSymbol.java).
// später kann Neo4j zusätzliche Dinge liefern:
//
//distance
//path length
//centrality score
//graph labels
//
//Daher PostgreSQL-Symbol und Graph-Symbol sind getrennt
