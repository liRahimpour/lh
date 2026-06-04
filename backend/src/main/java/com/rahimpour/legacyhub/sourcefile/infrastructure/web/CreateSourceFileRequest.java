package com.rahimpour.legacyhub.sourcefile.infrastructure.web;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSourceFileRequest(

        @NotBlank(message = "Path must not be empty")
        String path,

        @NotBlank(message = "Filename must not be empty")
        @Size(max = 255, message = "Filename must not exceed 255 characters")
        String filename,

        @Size(max = 50, message = "Extension must not exceed 50 characters")
        String extension,

        @NotNull(message = "Language must not be null")
        SourceFileLanguage language,

        @Min(value = 0, message = "sizeBytes must not be negative")
        long sizeBytes,

        @Size(max = 128, message = "contentHash must not exceed 128 characters")
        String contentHash,

        String storageKey
) {
}
