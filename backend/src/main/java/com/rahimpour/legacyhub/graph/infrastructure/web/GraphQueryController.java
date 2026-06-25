package com.rahimpour.legacyhub.graph.infrastructure.web;

import com.rahimpour.legacyhub.graph.application.GraphQueryService;
import com.rahimpour.legacyhub.graph.domain.GraphNeighborhood;
import com.rahimpour.legacyhub.graph.domain.GraphOverview;
import com.rahimpour.legacyhub.graph.domain.GraphSymbol;
import com.rahimpour.legacyhub.graph.domain.GraphTraversalResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/graph")
public class GraphQueryController {

    private final GraphQueryService graphQueryService;

    public GraphQueryController(GraphQueryService graphQueryService) {
        this.graphQueryService = graphQueryService;
    }

    @GetMapping("/overview")
    public GraphOverview getOverview(@PathVariable UUID projectId) {
        return graphQueryService.getProjectOverview(projectId);
    }

    @GetMapping("/symbols/{symbolId}/neighbors")
    public ResponseEntity<GraphNeighborhood> getNeighbors(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId
    ) {
        try {
            return ResponseEntity.ok(
                    graphQueryService.getNeighborhood(projectId, symbolId)
            );
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/symbols/{symbolId}/dependencies")
    public GraphTraversalResult getDependencies(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId,
            @RequestParam(required = false) Integer depth
    ) {
        return graphQueryService.getDependencies(projectId, symbolId, depth);
    }

    @GetMapping("/symbols/{symbolId}/impact")
    public GraphTraversalResult getImpact(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId,
            @RequestParam(required = false) Integer depth
    ) {
        return graphQueryService.getImpact(projectId, symbolId, depth);
    }
}
