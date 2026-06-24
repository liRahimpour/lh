package com.rahimpour.legacyhub.graph.application;

import com.rahimpour.legacyhub.graph.ports.GraphHealthPort;
import org.springframework.stereotype.Service;

@Service
public class GraphHealthService {

    private final GraphHealthPort graphHealthPort;

    public GraphHealthService(GraphHealthPort graphHealthPort) {
        this.graphHealthPort = graphHealthPort;
    }

    public boolean isGraphAvailable() {
        return graphHealthPort.isAvailable();
    }
}
