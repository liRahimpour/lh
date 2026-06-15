package com.rahimpour.legacyhub.coderelation.infrastructure.persistence;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.coderelation.ports.CodeRelationRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CodeRelationPersistenceAdapter implements CodeRelationRepositoryPort {

    private final CodeRelationJpaRepository jpaRepository;

    public CodeRelationPersistenceAdapter(CodeRelationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CodeRelation save(CodeRelation relation) {
        CodeRelationEntity entity = toEntity(relation);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<CodeRelation> saveAll(List<CodeRelation> relations) {
        List<CodeRelationEntity> entities = relations.stream()
                .map(this::toEntity)
                .toList();

        return jpaRepository.saveAll(entities)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CodeRelation> findByProjectId(UUID projectId) {
        return jpaRepository.findByProjectId(projectId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CodeRelation> findByProjectIdAndSourceSymbolId(UUID projectId, UUID sourceSymbolId) {
        return jpaRepository.findByProjectIdAndSourceSymbolId(projectId, sourceSymbolId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CodeRelation> findByProjectIdAndTargetSymbolId(UUID projectId, UUID targetSymbolId) {
        return jpaRepository.findByProjectIdAndTargetSymbolId(projectId, targetSymbolId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<CodeRelation> findByProjectIdAndId(UUID projectId, UUID relationId) {
        return jpaRepository.findByProjectIdAndId(projectId, relationId)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByProjectId(UUID projectId) {
        jpaRepository.deleteByProjectId(projectId);
    }

    private CodeRelationEntity toEntity(CodeRelation relation) {
        return new CodeRelationEntity(
                relation.getId(),
                relation.getProjectId(),
                relation.getSourceSymbolId(),
                relation.getTargetSymbolId(),
                relation.getType(),
                relation.getCreatedAt()
        );
    }

    private CodeRelation toDomain(CodeRelationEntity entity) {
        return new CodeRelation(
                entity.getId(),
                entity.getProjectId(),
                entity.getSourceSymbolId(),
                entity.getTargetSymbolId(),
                entity.getType(),
                entity.getCreatedAt()
        );
    }
}
