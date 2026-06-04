package com.rahimpour.legacyhub.sourcefile.application;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import com.rahimpour.legacyhub.sourcefile.ports.SourceFileRepositoryPort;
import com.rahimpour.legacyhub.project.application.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SourceFileService {

    private final SourceFileRepositoryPort sourceFileRepository;
    private final ProjectService projectService;

    public SourceFileService(
            SourceFileRepositoryPort sourceFileRepository,
            ProjectService projectService
    ) {
        this.sourceFileRepository = sourceFileRepository;
        this.projectService = projectService;
    }

    public SourceFile createSourceFile(
            UUID projectId,
            String path,
            String filename,
            String extension,
            SourceFileLanguage language,
            long sizeBytes,
            String contentHash,
            String storageKey
    ) {
        // Ensures project exists. If not, ProjectService throws exception.
        projectService.getProjectById(projectId);

        SourceFile sourceFile = SourceFile.create(
                projectId,
                path,
                filename,
                extension,
                language,
                sizeBytes,
                contentHash,
                storageKey
        );

        return sourceFileRepository.save(sourceFile);
    }

    public List<SourceFile> getSourceFilesByProjectId(UUID projectId) {
        projectService.getProjectById(projectId);
        return sourceFileRepository.findByProjectId(projectId);
    }

    public SourceFile getSourceFileById(UUID projectId, UUID sourceFileId) {
        projectService.getProjectById(projectId);

        return sourceFileRepository.findByProjectIdAndId(projectId, sourceFileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Source file not found: " + sourceFileId + " for project: " + projectId
                ));
    }
}
