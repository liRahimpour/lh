package com.rahimpour.legacyhub.graph.application;

import com.rahimpour.legacyhub.graph.domain.GraphNeighborhood;
import com.rahimpour.legacyhub.graph.domain.GraphOverview;
import com.rahimpour.legacyhub.graph.domain.GraphSymbol;
import com.rahimpour.legacyhub.graph.domain.GraphTraversalResult;
import com.rahimpour.legacyhub.graph.ports.CodeGraphQueryPort;
import com.rahimpour.legacyhub.project.application.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GraphQueryService {

    private static final int DEFAULT_MAX_DEPTH = 3;
    private static final int MAX_ALLOWED_DEPTH = 5;

    private final ProjectService projectService;
    private final CodeGraphQueryPort codeGraphQueryPort;

    public GraphQueryService(
            ProjectService projectService,
            CodeGraphQueryPort codeGraphQueryPort
    ) {
        this.projectService = projectService;
        this.codeGraphQueryPort = codeGraphQueryPort;
    }

    public GraphOverview getProjectOverview(UUID projectId) {
        projectService.getProjectById(projectId);
        return codeGraphQueryPort.getProjectOverview(projectId);
    }

    public GraphNeighborhood getNeighborhood(UUID projectId, UUID symbolId) {
        projectService.getProjectById(projectId);

        return codeGraphQueryPort.findNeighborhood(projectId, symbolId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Graph symbol not found: " + symbolId + " for project: " + projectId
                ));
    }

    public GraphTraversalResult getDependencies(
            UUID projectId,
            UUID symbolId,
            Integer requestedDepth
    ) {
        projectService.getProjectById(projectId);

        int depth = normalizeDepth(requestedDepth);

        return codeGraphQueryPort.findDependencies(
                projectId,
                symbolId,
                depth
        );
    }

    public GraphTraversalResult getImpact(
            UUID projectId,
            UUID symbolId,
            Integer requestedDepth
    ) {
        projectService.getProjectById(projectId);

        int depth = normalizeDepth(requestedDepth);

        return codeGraphQueryPort.findImpact(
                projectId,
                symbolId,
                depth
        );
    }

    private int normalizeDepth(Integer requestedDepth) {
        if (requestedDepth == null) {
            return DEFAULT_MAX_DEPTH;
        }

        if (requestedDepth < 1 || requestedDepth > MAX_ALLOWED_DEPTH) {
            throw new IllegalArgumentException(
                    "depth must be between 1 and " + MAX_ALLOWED_DEPTH
            );
        }

        return requestedDepth;
    }
}

// depth ist für MVP begrenzt
//
//Weil Graph-Abfragen mit große Tiefe schnell sehr viele Wege finden können.
//
//Für eine Demo und MVP reicht esrtmal
//
//Tiefe 1 = direkte Beziehungen
//Tiefe 2 = Beziehungen von Beziehungen
//Tiefe 3 =  Impact-Ansicht
