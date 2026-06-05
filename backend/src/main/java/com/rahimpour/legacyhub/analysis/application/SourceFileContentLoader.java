package com.rahimpour.legacyhub.analysis.application;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.storage.application.StorageService;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class SourceFileContentLoader {

    private final StorageService storageService;

    public SourceFileContentLoader(StorageService storageService) {
        this.storageService = storageService;
    }

    public String loadContent(SourceFile sourceFile) {
        if (sourceFile.getStorageKey() == null || sourceFile.getStorageKey().isBlank()) {
            throw new IllegalArgumentException("Source file has no storageKey: " + sourceFile.getId());
        }
        //TODO: Für MVP UTF-8. Später brauchen wir Encoding-Erkennung, besonders bei alten Delphi-Projekten.
        try (InputStream inputStream = storageService.load(sourceFile.getStorageKey())) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load source file content: " + sourceFile.getStorageKey(), e);
        }
    }
}
