package com.rahimpour.legacyhub.coderelation.ports;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeRelationRepositoryPort {

    CodeRelation save(CodeRelation relation);

    List<CodeRelation> saveAll(List<CodeRelation> relations);

    List<CodeRelation> findByProjectId(UUID projectId);

    List<CodeRelation> findByProjectIdAndSourceSymbolId(UUID projectId, UUID sourceSymbolId);

    List<CodeRelation> findByProjectIdAndTargetSymbolId(UUID projectId, UUID targetSymbolId);

    Optional<CodeRelation> findByProjectIdAndId(UUID projectId, UUID relationId);

    void deleteByProjectId(UUID projectId);
}
