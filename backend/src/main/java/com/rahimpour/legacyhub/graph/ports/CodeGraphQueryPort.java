package com.rahimpour.legacyhub.graph.ports;

import com.rahimpour.legacyhub.graph.domain.GraphNeighborhood;
import com.rahimpour.legacyhub.graph.domain.GraphOverview;
import com.rahimpour.legacyhub.graph.domain.GraphSymbol;
import com.rahimpour.legacyhub.graph.domain.GraphTraversalResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeGraphQueryPort {

    GraphOverview getProjectOverview(UUID projectId);

    Optional<GraphNeighborhood> findNeighborhood(UUID projectId, UUID symbolId);

    GraphTraversalResult findDependencies(UUID projectId, UUID symbolId, int maxDepth);

    GraphTraversalResult findImpact(UUID projectId, UUID symbolId, int maxDepth);
}

//Gib mir Übersicht, Nachbarschaften, Abhängigkeiten und impacts.