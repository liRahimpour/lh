package com.rahimpour.legacyhub.analysis.relation.application;

import com.rahimpour.legacyhub.analysis.relation.domain.DetectedRelation;
import com.rahimpour.legacyhub.analysis.relation.domain.SourceFileRelationAnalyzer;
import com.rahimpour.legacyhub.coderelation.application.CodeRelationService;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.codesymbol.application.CodeSymbolService;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.sourcefile.application.SourceFileService;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RelationAnalysisService {

    private final CodeSymbolService codeSymbolService;
    private final CodeRelationService codeRelationService;
    private final SourceFileService sourceFileService;
    private final List<SourceFileRelationAnalyzer> analyzers;

    public RelationAnalysisService(
            CodeSymbolService codeSymbolService,
            CodeRelationService codeRelationService,
            SourceFileService sourceFileService,
            List<SourceFileRelationAnalyzer> analyzers
    ) {
        this.codeSymbolService = codeSymbolService;
        this.codeRelationService = codeRelationService;
        this.sourceFileService = sourceFileService;
        this.analyzers = analyzers;
    }

    @Transactional
    public RelationAnalysisResult analyzeProject(UUID projectId) {
        List<CodeSymbol> symbols = codeSymbolService.getSymbolsByProjectId(projectId);
        List<SourceFile> sourceFiles = sourceFileService.getSourceFilesByProjectId(projectId);

        codeRelationService.deleteRelationsByProjectId(projectId);

        Map<UUID, SourceFile> sourceFileById = sourceFiles.stream()
                .collect(Collectors.toMap(SourceFile::getId, sourceFile -> sourceFile));

        Map<UUID, List<CodeSymbol>> symbolsBySourceFileId = symbols.stream()
                .collect(Collectors.groupingBy(CodeSymbol::getSourceFileId));

        int analyzedFiles = 0;
        int skippedFiles = 0;
        List<CodeRelation> relationsToSave = new ArrayList<>();

        for (Map.Entry<UUID, List<CodeSymbol>> entry : symbolsBySourceFileId.entrySet()) {
            UUID sourceFileId = entry.getKey();
            List<CodeSymbol> fileSymbols = entry.getValue();

            SourceFile sourceFile = sourceFileById.get(sourceFileId);

            if (sourceFile == null) {
                skippedFiles++;
                continue;
            }

            SourceFileRelationAnalyzer analyzer = findAnalyzer(sourceFile.getLanguage());

            if (analyzer == null) {
                skippedFiles++;
                continue;
            }

            List<DetectedRelation> detectedRelations = analyzer.analyze(fileSymbols);

            List<CodeRelation> codeRelations = detectedRelations.stream()
                    .map(detected -> CodeRelation.create(
                            projectId,
                            detected.sourceSymbolId(),
                            detected.targetSymbolId(),
                            detected.type()
                    ))
                    .toList();

            relationsToSave.addAll(codeRelations);
            analyzedFiles++;
        }

        List<CodeRelation> savedRelations = codeRelationService.createRelations(relationsToSave);

        return new RelationAnalysisResult(
                projectId,
                analyzedFiles,
                skippedFiles,
                savedRelations.size()
        );
    }

    private SourceFileRelationAnalyzer findAnalyzer(com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage language) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.supports(language))
                .findFirst()
                .orElse(null);
    }

    public record RelationAnalysisResult(
            UUID projectId,
            int analyzedFiles,
            int skippedFiles,
            int detectedRelations
    ) {
    }
}
