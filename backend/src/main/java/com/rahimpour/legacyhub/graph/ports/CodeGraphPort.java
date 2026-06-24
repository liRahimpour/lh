package com.rahimpour.legacyhub.graph.ports;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;

import java.util.List;
import java.util.UUID;

public interface CodeGraphPort {

    void syncSymbols(UUID projectId, List<CodeSymbol> symbols);

    void deleteProjectGraph(UUID projectId);
}
