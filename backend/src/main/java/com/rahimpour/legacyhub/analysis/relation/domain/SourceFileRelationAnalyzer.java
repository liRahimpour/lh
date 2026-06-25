package com.rahimpour.legacyhub.analysis.relation.domain;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;

import java.util.List;

import java.util.List;

import java.util.List;

public interface SourceFileRelationAnalyzer {

    boolean supports(SourceFileLanguage language);

    List<DetectedRelation> analyze(RelationAnalysisContext context);
}// wie bei Sourcefile symbol
