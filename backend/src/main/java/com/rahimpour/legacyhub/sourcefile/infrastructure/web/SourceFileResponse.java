package com.rahimpour.legacyhub.sourcefile.infrastructure.web;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;

import java.time.Instant;
import java.util.UUID;

public record SourceFileResponse(
        UUID id,
        UUID projectId,
        String path,
        String filename,
        String extension,
        SourceFileLanguage language,
        long sizeBytes,
        String contentHash,
        String storageKey,
        Instant createdAt
) {

    public static SourceFileResponse from(SourceFile sourceFile) {
        return new SourceFileResponse(
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
}
