package com.rahimpour.legacyhub.codesymbol.infrastructure.web;

import com.rahimpour.legacyhub.codesymbol.application.CodeSymbolService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/symbols")
public class CodeSymbolController {

    private final CodeSymbolService codeSymbolService;

    public CodeSymbolController(CodeSymbolService codeSymbolService) {
        this.codeSymbolService = codeSymbolService;
    }

    @GetMapping
    public List<CodeSymbolResponse> getSymbolsByProjectId(@PathVariable UUID projectId) {
        return codeSymbolService.getSymbolsByProjectId(projectId)
                .stream()
                .map(CodeSymbolResponse::from)
                .toList();
    }

    @GetMapping("/{symbolId}")
    public CodeSymbolResponse getSymbolById(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId
    ) {
        return CodeSymbolResponse.from(codeSymbolService.getSymbolById(projectId, symbolId));
    }
}
