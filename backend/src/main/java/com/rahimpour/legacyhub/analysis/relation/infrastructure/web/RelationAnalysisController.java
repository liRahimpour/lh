package com.rahimpour.legacyhub.analysis.relation.infrastructure.web;

import com.rahimpour.legacyhub.analysis.relation.application.RelationAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/analysis")
public class RelationAnalysisController {

    private final RelationAnalysisService relationAnalysisService;

    public RelationAnalysisController(RelationAnalysisService relationAnalysisService) {
        this.relationAnalysisService = relationAnalysisService;
    }

    @PostMapping("/relations")
    @ResponseStatus(HttpStatus.CREATED)
    public RelationAnalysisResponse analyzeRelations(@PathVariable UUID projectId) {
        RelationAnalysisService.RelationAnalysisResult result =
                relationAnalysisService.analyzeProject(projectId);

        return new RelationAnalysisResponse(
                result.projectId(),
                result.analyzedFiles(),
                result.skippedFiles(),
                result.detectedRelations()
        );
    }
}
