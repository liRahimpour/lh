package com.rahimpour.legacyhub.upload.infrastructure.web;

import java.util.UUID;

public record SourceArchiveUploadResponse(
        UUID projectId,
        String archiveStorageKey,
        int importedFiles,
        int skippedFiles
) {
}
