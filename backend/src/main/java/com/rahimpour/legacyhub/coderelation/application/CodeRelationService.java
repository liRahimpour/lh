package com.rahimpour.legacyhub.coderelation.application;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.coderelation.domain.CodeRelationType;
import com.rahimpour.legacyhub.coderelation.ports.CodeRelationRepositoryPort;
import com.rahimpour.legacyhub.project.application.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CodeRelationService {

    private final CodeRelationRepositoryPort relationRepository;
    private final ProjectService projectService;

    public CodeRelationService(
            CodeRelationRepositoryPort relationRepository,
            ProjectService projectService
    ) {
        this.relationRepository = relationRepository;
        this.projectService = projectService;
    }

    public CodeRelation createRelation(
            UUID projectId,
            UUID sourceSymbolId,
            UUID targetSymbolId,
            CodeRelationType type
    ) {
        projectService.getProjectById(projectId);

        CodeRelation relation = CodeRelation.create(
                projectId,
                sourceSymbolId,
                targetSymbolId,
                type
        );

        return relationRepository.save(relation);
    }

    public List<CodeRelation> createRelations(List<CodeRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return List.of();
        }

        return relationRepository.saveAll(relations);
    }

    public List<CodeRelation> getRelationsByProjectId(UUID projectId) {
        projectService.getProjectById(projectId);
        return relationRepository.findByProjectId(projectId);
    }

    public List<CodeRelation> getOutgoingRelations(UUID projectId, UUID symbolId) {
        projectService.getProjectById(projectId);
        return relationRepository.findByProjectIdAndSourceSymbolId(projectId, symbolId);
    }

    public List<CodeRelation> getIncomingRelations(UUID projectId, UUID symbolId) {
        projectService.getProjectById(projectId);
        return relationRepository.findByProjectIdAndTargetSymbolId(projectId, symbolId);
    }

//    public CodeRelation getRelationById(UUID projectId, UUID relationId) {
//        projectService.getProjectById(projectId);
//
//        return relationRepository.findByProjectIdAndId(projectId, relationId)
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "Code relation not found: " + relationId + " for project: " + projectId
//                ));
//    } // TODO: Workaround, globale exaption muss irgendwann gebaut werden

    public Optional<CodeRelation> findRelationById(UUID projectId, UUID relationId) {
        projectService.getProjectById(projectId);
        return relationRepository.findByProjectIdAndId(projectId, relationId);
    }

    public void deleteRelationsByProjectId(UUID projectId) {
        projectService.getProjectById(projectId);
        relationRepository.deleteByProjectId(projectId);
    }
}
