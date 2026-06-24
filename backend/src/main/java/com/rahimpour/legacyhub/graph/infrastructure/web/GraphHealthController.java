package com.rahimpour.legacyhub.graph.infrastructure.web;

import com.rahimpour.legacyhub.graph.application.GraphHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/graph")
public class GraphHealthController {

    private final GraphHealthService graphHealthService;

    public GraphHealthController(GraphHealthService graphHealthService) {
        this.graphHealthService = graphHealthService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        if (graphHealthService.isGraphAvailable()) {
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "database", "neo4j"
            ));
        }

        return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "database", "neo4j"
        ));
    }
}
