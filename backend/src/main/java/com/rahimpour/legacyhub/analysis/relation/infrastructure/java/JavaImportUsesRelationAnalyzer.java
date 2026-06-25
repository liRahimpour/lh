package com.rahimpour.legacyhub.analysis.relation.infrastructure.java;

import com.rahimpour.legacyhub.analysis.relation.domain.DetectedRelation;
import com.rahimpour.legacyhub.analysis.relation.domain.RelationAnalysisContext;
import com.rahimpour.legacyhub.analysis.relation.domain.SourceFileRelationAnalyzer;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JavaImportUsesRelationAnalyzer implements SourceFileRelationAnalyzer {

    private static final Pattern IMPORT_PATTERN = Pattern.compile(
            "^\\s*import\\s+([a-zA-Z_][\\w.]*);\\s*$",
            Pattern.MULTILINE
    );

    @Override
    public boolean supports(SourceFileLanguage language) {
        return language == SourceFileLanguage.JAVA;
    }

    @Override
    public List<DetectedRelation> analyze(RelationAnalysisContext context) {
        Optional<CodeSymbol> sourceContainer = findMainContainer(
                context.sourceFileSymbols()
        );

        if (sourceContainer.isEmpty()) {
            return List.of();
        }

        Matcher matcher = IMPORT_PATTERN.matcher(context.sourceContent());

        return matcher.results()
                .map(result -> result.group(1))
                .map(importedFullyQualifiedName ->
                        findProjectTypeByFullyQualifiedName(
                                context.projectSymbols(),
                                importedFullyQualifiedName
                        )
                )
                .flatMap(Optional::stream)
                .filter(target -> !target.getId().equals(sourceContainer.get().getId()))
                .map(target -> new DetectedRelation(
                        sourceContainer.get().getId(),
                        target.getId(),
                        CodeRelationType.USES
                ))
                .distinct()
                .toList();
    }

    private Optional<CodeSymbol> findMainContainer(List<CodeSymbol> symbols) {
        return symbols.stream()
                .filter(this::isContainer)
                .findFirst();
    }

    private boolean isContainer(CodeSymbol symbol) {
        return symbol.getType() == CodeSymbolType.CLASS
                || symbol.getType() == CodeSymbolType.INTERFACE
                || symbol.getType() == CodeSymbolType.RECORD
                || symbol.getType() == CodeSymbolType.ENUM;
    }

    private Optional<CodeSymbol> findProjectTypeByFullyQualifiedName(
            List<CodeSymbol> projectSymbols,
            String importedFullyQualifiedName
    ) {
        return projectSymbols.stream()
                .filter(this::isContainer)
                .filter(symbol -> importedFullyQualifiedName.equals(
                        symbol.getFullyQualifiedName()
                ))
                .findFirst();
    }
}
