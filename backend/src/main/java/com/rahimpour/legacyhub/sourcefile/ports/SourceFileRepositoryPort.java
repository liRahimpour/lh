package com.rahimpour.legacyhub.sourcefile.ports;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SourceFileRepositoryPort {

    SourceFile save(SourceFile sourceFile);

    List<SourceFile> findByProjectId(UUID projectId);

    Optional<SourceFile> findByProjectIdAndId(UUID projectId, UUID sourceFileId);
}
