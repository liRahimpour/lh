package com.rahimpour.legacyhub.upload.application;

import com.rahimpour.legacyhub.sourcefile.application.FileLanguageDetector;
import com.rahimpour.legacyhub.sourcefile.application.SourceFileService;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import com.rahimpour.legacyhub.storage.application.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class SourceFileUploadService {

    private final StorageService storageService;
    private final SourceFileService sourceFileService;
    private final FileLanguageDetector fileLanguageDetector;

    public SourceFileUploadService(
            StorageService storageService,
            SourceFileService sourceFileService, FileLanguageDetector fileLanguageDetector
    ) {
        this.storageService = storageService;
        this.sourceFileService = sourceFileService;
        this.fileLanguageDetector = fileLanguageDetector;
    }

    public SourceFile uploadSourceFile(UUID projectId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file must not be empty");
        }

        try {
            byte[] bytes = file.getBytes();

            String originalFilename = normalizeFilename(file.getOriginalFilename());
            String extension = fileLanguageDetector.extractExtension(originalFilename);
            SourceFileLanguage language = fileLanguageDetector.detectByFilename(originalFilename);
            String contentHash = sha256(bytes);
            String storageKey = buildStorageKey(projectId, originalFilename);

            storageService.store(
                    storageKey,
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream"
            );

            return sourceFileService.createSourceFile(
                    projectId,
                    originalFilename,
                    originalFilename,
                    extension,
                    language,
                    bytes.length,
                    contentHash,
                    storageKey
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload source file", e);
        }
    }

    private String normalizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Original filename must not be empty");
        }

        return originalFilename.replace("\\", "/");
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate SHA-256 hash", e);
        }
    }

    private String buildStorageKey(UUID projectId, String filename) {
        return "projects/" + projectId + "/source/" + filename;
    }
}
