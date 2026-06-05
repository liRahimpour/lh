package com.rahimpour.legacyhub.codesymbol.ports;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeSymbolRepositoryPort {

    CodeSymbol save(CodeSymbol codeSymbol);

    List<CodeSymbol> saveAll(List<CodeSymbol> codeSymbols);

    List<CodeSymbol> findByProjectId(UUID projectId);

    Optional<CodeSymbol> findByProjectIdAndId(UUID projectId, UUID symbolId);

    void deleteByProjectId(UUID projectId);
}
