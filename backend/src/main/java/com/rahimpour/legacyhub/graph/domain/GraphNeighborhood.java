package com.rahimpour.legacyhub.graph.domain;

import java.util.List;

public record GraphNeighborhood(
        GraphSymbol center,
        List<GraphConnection> outgoing,
        List<GraphConnection> incoming
) {
}

//Was hängt direkt an diesem Symbol?