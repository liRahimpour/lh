package com.rahimpour.legacyhub.coderelation.infrastructure.web;

import com.rahimpour.legacyhub.coderelation.application.CodeRelationService;
import com.rahimpour.legacyhub.codesymbol.application.CodeSymbolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/relations")
public class CodeRelationController {

    private final CodeRelationService relationService;
    private final CodeSymbolService codeSymbolService;

    public CodeRelationController(
            CodeRelationService relationService,
            CodeSymbolService codeSymbolService
    ) {
        this.relationService = relationService;
        this.codeSymbolService = codeSymbolService;
    }

    @GetMapping
    public List<CodeRelationResponse> getRelationsByProjectId(@PathVariable UUID projectId) {
        return relationService.getRelationsByProjectId(projectId)
                .stream()
                .map(CodeRelationResponse::from)
                .toList();
    }

    @GetMapping("/{relationId}")
    public ResponseEntity<CodeRelationResponse> getRelationById(
            @PathVariable UUID projectId,
            @PathVariable UUID relationId
    ) {
        return relationService.findRelationById(projectId, relationId)
                .map(CodeRelationResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/symbols/{symbolId}/outgoing")
    public ResponseEntity<List<CodeRelationResponse>> getOutgoingRelations(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId
    ) {
        if (!symbolExists(projectId, symbolId)) {
            return ResponseEntity.notFound().build();
        }

        List<CodeRelationResponse> response = relationService.getOutgoingRelations(projectId, symbolId)
                .stream()
                .map(CodeRelationResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/symbols/{symbolId}/incoming")
    public ResponseEntity<List<CodeRelationResponse>> getIncomingRelations(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId
    ) {
        if (!symbolExists(projectId, symbolId)) {
            return ResponseEntity.notFound().build();
        }

        List<CodeRelationResponse> response = relationService.getIncomingRelations(projectId, symbolId)
                .stream()
                .map(CodeRelationResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    private boolean symbolExists(UUID projectId, UUID symbolId) {
        try {
            codeSymbolService.getSymbolById(projectId, symbolId);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
} //TODO: exaptionhandling muss global gesetzt werden und dann controller und service angepasst werden
