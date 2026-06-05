package com.rahimpour.legacyhub.codesymbol.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeSymbolJpaRepository extends JpaRepository<CodeSymbolEntity, UUID> {

    List<CodeSymbolEntity> findByProjectId(UUID projectId);

    Optional<CodeSymbolEntity> findByProjectIdAndId(UUID projectId, UUID id);

    void deleteByProjectId(UUID projectId);
}
