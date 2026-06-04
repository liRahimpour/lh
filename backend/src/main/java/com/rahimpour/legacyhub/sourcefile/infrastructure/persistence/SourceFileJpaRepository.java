package com.rahimpour.legacyhub.sourcefile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SourceFileJpaRepository extends JpaRepository<SourceFileEntity, UUID> {

    List<SourceFileEntity> findByProjectId(UUID projectId);

    Optional<SourceFileEntity> findByProjectIdAndId(UUID projectId, UUID id);
}