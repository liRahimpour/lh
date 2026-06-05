package com.rahimpour.legacyhub.analysis.application;

import com.rahimpour.legacyhub.analysis.domain.DetectedSymbol;
import com.rahimpour.legacyhub.analysis.domain.SourceFileAnalyzer;
import com.rahimpour.legacyhub.codesymbol.application.CodeSymbolService;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.sourcefile.application.SourceFileService;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SymbolAnalysisService {

    private final SourceFileService sourceFileService;
    private final SourceFileContentLoader contentLoader;
    private final CodeSymbolService codeSymbolService;
    private final List<SourceFileAnalyzer> analyzers;

    public SymbolAnalysisService(
            SourceFileService sourceFileService,
            SourceFileContentLoader contentLoader,
            CodeSymbolService codeSymbolService,
            List<SourceFileAnalyzer> analyzers
    ) {
        this.sourceFileService = sourceFileService;
        this.contentLoader = contentLoader;
        this.codeSymbolService = codeSymbolService;
        this.analyzers = analyzers;
    }

    @Transactional
    public SymbolAnalysisResult analyzeProject(UUID projectId) {
        List<SourceFile> sourceFiles = sourceFileService.getSourceFilesByProjectId(projectId);

        codeSymbolService.deleteSymbolsByProjectId(projectId);

        int analyzedFiles = 0;
        int skippedFiles = 0;
        List<CodeSymbol> allSymbols = new ArrayList<>();

        for (SourceFile sourceFile : sourceFiles) {
            SourceFileAnalyzer analyzer = findAnalyzer(sourceFile);

            if (analyzer == null) {
                skippedFiles++;
                continue;
            }

            String content = contentLoader.loadContent(sourceFile);
            List<DetectedSymbol> detectedSymbols = analyzer.analyze(sourceFile, content);

            List<CodeSymbol> symbols = detectedSymbols.stream()
                    .map(detected -> CodeSymbol.create(
                            projectId,
                            sourceFile.getId(),
                            detected.type(),
                            detected.name(),
                            detected.fullyQualifiedName(),
                            detected.startLine(),
                            detected.endLine()
                    ))
                    .toList();

            allSymbols.addAll(symbols);
            analyzedFiles++;
        }

        List<CodeSymbol> savedSymbols = codeSymbolService.createCodeSymbols(allSymbols);

        return new SymbolAnalysisResult(
                projectId,
                analyzedFiles,
                skippedFiles,
                savedSymbols.size()
        );
    }

    private SourceFileAnalyzer findAnalyzer(SourceFile sourceFile) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.supports(sourceFile.getLanguage()))
                .findFirst()
                .orElse(null);
    }

    public record SymbolAnalysisResult(
            UUID projectId,
            int analyzedFiles,
            int skippedFiles,
            int detectedSymbols
    ) {
    }
}
