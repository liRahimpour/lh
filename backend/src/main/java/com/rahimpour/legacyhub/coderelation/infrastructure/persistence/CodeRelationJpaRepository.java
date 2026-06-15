package com.rahimpour.legacyhub.coderelation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeRelationJpaRepository extends JpaRepository<CodeRelationEntity, UUID> {

    List<CodeRelationEntity> findByProjectId(UUID projectId);

    List<CodeRelationEntity> findByProjectIdAndSourceSymbolId(UUID projectId, UUID sourceSymbolId);

    List<CodeRelationEntity> findByProjectIdAndTargetSymbolId(UUID projectId, UUID targetSymbolId);

    Optional<CodeRelationEntity> findByProjectIdAndId(UUID projectId, UUID id);

    void deleteByProjectId(UUID projectId);
}