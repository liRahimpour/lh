package com.rahimpour.legacyhub.codesymbol.infrastructure.persistence;

import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.ports.CodeSymbolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CodeSymbolPersistenceAdapter implements CodeSymbolRepositoryPort {

    private final CodeSymbolJpaRepository jpaRepository;

    public CodeSymbolPersistenceAdapter(CodeSymbolJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CodeSymbol save(CodeSymbol codeSymbol) {
        CodeSymbolEntity entity = toEntity(codeSymbol);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<CodeSymbol> saveAll(List<CodeSymbol> codeSymbols) {
        List<CodeSymbolEntity> entities = codeSymbols.stream()
                .map(this::toEntity)
                .toList();

        return jpaRepository.saveAll(entities)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CodeSymbol> findByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<CodeSymbol> findByProjectIdAndId(UUID projectId, UUID symbolId) {
        return jpaRepository.findByProjectIdAndId(projectId, symbolId)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByProjectId(UUID projectId) {
        jpaRepository.deleteByProjectId(projectId);
    }

    private CodeSymbolEntity toEntity(CodeSymbol symbol) {
        return new CodeSymbolEntity(
                symbol.getId(),
                symbol.getProjectId(),
                symbol.getSourceFileId(),
                symbol.getType(),
                symbol.getName(),
                symbol.getFullyQualifiedName(),
                symbol.getStartLine(),
                symbol.getEndLine(),
                symbol.getCreatedAt()
        );
    }

    private CodeSymbol toDomain(CodeSymbolEntity entity) {
        return new CodeSymbol(
                entity.getId(),
                entity.getProjectId(),
                entity.getSourceFileId(),
                entity.getType(),
                entity.getName(),
                entity.getFullyQualifiedName(),
                entity.getStartLine(),
                entity.getEndLine(),
                entity.getCreatedAt()
        );
    }
}
