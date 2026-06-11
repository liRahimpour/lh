package com.rahimpour.legacyhub.analysis.relation.infrastructure.java;

import com.rahimpour.legacyhub.analysis.relation.domain.DetectedRelation;
import com.rahimpour.legacyhub.analysis.relation.domain.SourceFileRelationAnalyzer;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class JavaSourceFileRelationAnalyzer implements SourceFileRelationAnalyzer {

    @Override
    public boolean supports(SourceFileLanguage language) {
        return language == SourceFileLanguage.JAVA;
    }

    @Override
    public List<DetectedRelation> analyze(List<CodeSymbol> symbols) {
        Optional<CodeSymbol> containerSymbol = findMainContainer(symbols);

        if (containerSymbol.isEmpty()) {
            return List.of();
        }

        CodeSymbol container = containerSymbol.get();

        return symbols.stream()
                .filter(symbol -> !symbol.getId().equals(container.getId()))
                .filter(this::isMemberSymbol)
                .map(symbol -> new DetectedRelation(
                        container.getId(),
                        symbol.getId(),
                        relationTypeFor(symbol)
                ))
                .toList();
    }

    private Optional<CodeSymbol> findMainContainer(List<CodeSymbol> symbols) {
        return symbols.stream()
                .filter(this::isContainerSymbol)
                .min(Comparator.comparing(symbol -> symbol.getStartLine() != null ? symbol.getStartLine() : Integer.MAX_VALUE));
    }

    private boolean isContainerSymbol(CodeSymbol symbol) {
        return symbol.getType() == CodeSymbolType.CLASS
                || symbol.getType() == CodeSymbolType.INTERFACE
                || symbol.getType() == CodeSymbolType.RECORD
                || symbol.getType() == CodeSymbolType.ENUM;
    }

    private boolean isMemberSymbol(CodeSymbol symbol) {
        return symbol.getType() == CodeSymbolType.METHOD
                || symbol.getType() == CodeSymbolType.FIELD;
    }

    private CodeRelationType relationTypeFor(CodeSymbol symbol) {
        if (symbol.getType() == CodeSymbolType.FIELD) {
            return CodeRelationType.HAS_FIELD;
        }

        if (symbol.getType() == CodeSymbolType.METHOD) {
            return CodeRelationType.HAS_METHOD;
        }

        return CodeRelationType.UNKNOWN;
    }
}

//TODO: aktuell nur methoden gefunden die body haben, muss verbesert werden
