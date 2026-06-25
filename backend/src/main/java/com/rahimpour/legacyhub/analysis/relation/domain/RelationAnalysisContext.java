package com.rahimpour.legacyhub.analysis.relation.domain;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;

import java.util.List;

public record RelationAnalysisContext(
        SourceFile sourceFile,
        String sourceContent,
        List<CodeSymbol> sourceFileSymbols,
        List<CodeSymbol> projectSymbols
) {
}
