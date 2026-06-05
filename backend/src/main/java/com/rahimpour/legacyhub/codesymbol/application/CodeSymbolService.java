package com.rahimpour.legacyhub.codesymbol.application;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import com.rahimpour.legacyhub.codesymbol.ports.CodeSymbolRepositoryPort;
import com.rahimpour.legacyhub.project.application.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CodeSymbolService {

    private final CodeSymbolRepositoryPort codeSymbolRepository;
    private final ProjectService projectService;

    public CodeSymbolService(
            CodeSymbolRepositoryPort codeSymbolRepository,
            ProjectService projectService
    ) {
        this.codeSymbolRepository = codeSymbolRepository;
        this.projectService = projectService;
    }

    public CodeSymbol createCodeSymbol(
            UUID projectId,
            UUID sourceFileId,
            CodeSymbolType type,
            String name,
            String fullyQualifiedName,
            Integer startLine,
            Integer endLine
    ) {
        projectService.getProjectById(projectId);

        CodeSymbol symbol = CodeSymbol.create(
                projectId,
                sourceFileId,
                type,
                name,
                fullyQualifiedName,
                startLine,
                endLine
        );

        return codeSymbolRepository.save(symbol);
    }

    public List<CodeSymbol> createCodeSymbols(List<CodeSymbol> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            return List.of();
        }

        return codeSymbolRepository.saveAll(symbols);
    }

    public List<CodeSymbol> getSymbolsByProjectId(UUID projectId) {
        projectService.getProjectById(projectId);
        return codeSymbolRepository.findByProjectId(projectId);
    }

    public CodeSymbol getSymbolById(UUID projectId, UUID symbolId) {
        projectService.getProjectById(projectId);

        return codeSymbolRepository.findByProjectIdAndId(projectId, symbolId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Code symbol not found: " + symbolId + " for project: " + projectId
                ));
    }

    public void deleteSymbolsByProjectId(UUID projectId) {
        projectService.getProjectById(projectId);
        codeSymbolRepository.deleteByProjectId(projectId);
    }
}

//TODO: analysis_jobs Tabelle - Status: RUNNING / COMPLETED / FAILED - asynchrone Analyse - Fehler pro Datei speichern -Analyzer-Version speichern