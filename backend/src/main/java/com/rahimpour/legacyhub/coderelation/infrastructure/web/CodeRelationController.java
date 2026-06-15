package com.rahimpour.legacyhub.coderelation.infrastructure.web;

import com.rahimpour.legacyhub.coderelation.application.CodeRelationService;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.codesymbol.application.CodeSymbolService;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
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
    public List<EnrichedCodeRelationResponse> getRelationsByProjectId(@PathVariable UUID projectId) {
        return relationService.getRelationsByProjectId(projectId)
                .stream()
                .map(relation -> toEnrichedResponse(projectId, relation))
                .toList();
    }

    @GetMapping("/{relationId}")
    public ResponseEntity<EnrichedCodeRelationResponse> getRelationById(
            @PathVariable UUID projectId,
            @PathVariable UUID relationId
    ) {
        return relationService.findRelationById(projectId, relationId)
                .map(relation -> toEnrichedResponse(projectId, relation))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/symbols/{symbolId}/outgoing")
    public ResponseEntity<List<EnrichedCodeRelationResponse>> getOutgoingRelations(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId
    ) {
        if (!symbolExists(projectId, symbolId)) {
            return ResponseEntity.notFound().build();
        }

        List<EnrichedCodeRelationResponse> response = relationService.getOutgoingRelations(projectId, symbolId)
                .stream()
                .map(relation -> toEnrichedResponse(projectId, relation))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/symbols/{symbolId}/incoming")
    public ResponseEntity<List<EnrichedCodeRelationResponse>> getIncomingRelations(
            @PathVariable UUID projectId,
            @PathVariable UUID symbolId
    ) {
        if (!symbolExists(projectId, symbolId)) {
            return ResponseEntity.notFound().build();
        }

        List<EnrichedCodeRelationResponse> response = relationService.getIncomingRelations(projectId, symbolId)
                .stream()
                .map(relation -> toEnrichedResponse(projectId, relation))
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

    private EnrichedCodeRelationResponse toEnrichedResponse(UUID projectId, CodeRelation relation) {
        CodeSymbol sourceSymbol = codeSymbolService.getSymbolById(projectId, relation.getSourceSymbolId());
        CodeSymbol targetSymbol = codeSymbolService.getSymbolById(projectId, relation.getTargetSymbolId());

        return EnrichedCodeRelationResponse.from(
                relation,
                sourceSymbol,
                targetSymbol
        );
    }
} //TODO: exaptionhandling muss global gesetzt werden und dann controller und service angepasst werden
