package com.rahimpour.legacyhub.sourcefile.infrastructure.persistence;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.ports.SourceFileRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SourceFilePersistenceAdapter implements SourceFileRepositoryPort {

    private final SourceFileJpaRepository jpaRepository;

    public SourceFilePersistenceAdapter(SourceFileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SourceFile save(SourceFile sourceFile) {
        SourceFileEntity entity = toEntity(sourceFile);
        SourceFileEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<SourceFile> findByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<SourceFile> findByProjectIdAndId(UUID projectId, UUID sourceFileId) {
        return jpaRepository.findByProjectIdAndId(projectId, sourceFileId)
                .map(this::toDomain);
    }

    private SourceFileEntity toEntity(SourceFile sourceFile) {
        return new SourceFileEntity(
                sourceFile.getId(),
                sourceFile.getProjectId(),
                sourceFile.getPath(),
                sourceFile.getFilename(),
                sourceFile.getExtension(),
                sourceFile.getLanguage(),
                sourceFile.getSizeBytes(),
                sourceFile.getContentHash(),
                sourceFile.getStorageKey(),
                sourceFile.getCreatedAt()
        );
    }

    private SourceFile toDomain(SourceFileEntity entity) {
        return new SourceFile(
                entity.getId(),
                entity.getProjectId(),
                entity.getPath(),
                entity.getFilename(),
                entity.getExtension(),
                entity.getLanguage(),
                entity.getSizeBytes(),
                entity.getContentHash(),
                entity.getStorageKey(),
                entity.getCreatedAt()
        );
    }
}
