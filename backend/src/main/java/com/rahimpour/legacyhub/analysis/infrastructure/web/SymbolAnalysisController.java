package com.rahimpour.legacyhub.analysis.infrastructure.web;

import com.rahimpour.legacyhub.analysis.application.SymbolAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/analysis")
public class SymbolAnalysisController {

    private final SymbolAnalysisService symbolAnalysisService;

    public SymbolAnalysisController(SymbolAnalysisService symbolAnalysisService) {
        this.symbolAnalysisService = symbolAnalysisService;
    }

    @PostMapping("/symbols")
    @ResponseStatus(HttpStatus.CREATED)
    public SymbolAnalysisResponse analyzeSymbols(@PathVariable UUID projectId) {
        SymbolAnalysisService.SymbolAnalysisResult result =
                symbolAnalysisService.analyzeProject(projectId);

        return new SymbolAnalysisResponse(
                result.projectId(),
                result.analyzedFiles(),
                result.skippedFiles(),
                result.detectedSymbols()
        );
    }
}
