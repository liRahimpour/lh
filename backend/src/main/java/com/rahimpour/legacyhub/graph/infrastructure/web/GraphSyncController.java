package com.rahimpour.legacyhub.graph.infrastructure.web;

import com.rahimpour.legacyhub.graph.application.GraphSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/graph")
public class GraphSyncController {

    private final GraphSyncService graphSyncService;

    public GraphSyncController(GraphSyncService graphSyncService) {
        this.graphSyncService = graphSyncService;
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.CREATED)
    public GraphSyncResponse syncProject(@PathVariable UUID projectId) {
        GraphSyncService.GraphSyncResult result =
                graphSyncService.syncProjectSymbols(projectId);

        return new GraphSyncResponse(
                result.projectId(),
                result.syncedSymbols()
        );
    }
}
