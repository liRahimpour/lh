package com.rahimpour.legacyhub.analysis.domain;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;

import java.util.List;

public interface SourceFileAnalyzer {

    boolean supports(SourceFileLanguage language);

    List<DetectedSymbol> analyze(SourceFile sourceFile, String content);
}
