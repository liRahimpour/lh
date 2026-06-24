package com.rahimpour.legacyhub.graph.application;

import com.rahimpour.legacyhub.coderelation.application.CodeRelationService;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.codesymbol.application.CodeSymbolService;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.graph.ports.CodeGraphPort;
import com.rahimpour.legacyhub.project.application.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GraphSyncService {

    private final ProjectService projectService;
    private final CodeSymbolService codeSymbolService;
    private final CodeRelationService codeRelationService;
    private final CodeGraphPort codeGraphPort;

    public GraphSyncService(
            ProjectService projectService,
            CodeSymbolService codeSymbolService,
            CodeRelationService codeRelationService,
            CodeGraphPort codeGraphPort
    ) {
        this.projectService = projectService;
        this.codeSymbolService = codeSymbolService;
        this.codeRelationService = codeRelationService;
        this.codeGraphPort = codeGraphPort;
    }

    public GraphSyncResult syncProject(UUID projectId) {
        projectService.getProjectById(projectId);

        List<CodeSymbol> symbols = codeSymbolService.getSymbolsByProjectId(projectId);
        List<CodeRelation> relations = codeRelationService.getRelationsByProjectId(projectId);

        codeGraphPort.deleteProjectGraph(projectId);
        codeGraphPort.syncSymbols(projectId, symbols);
        codeGraphPort.syncRelations(projectId, relations);

        return new GraphSyncResult(
                projectId,
                symbols.size(),
                relations.size()
        );
    }

    public record GraphSyncResult(
            UUID projectId,
            int syncedSymbols,
            int syncedRelations
    ) {
    }
}
