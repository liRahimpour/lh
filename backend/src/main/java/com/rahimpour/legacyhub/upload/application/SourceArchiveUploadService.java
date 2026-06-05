package com.rahimpour.legacyhub.upload.application;

import com.rahimpour.legacyhub.sourcefile.application.FileLanguageDetector;
import com.rahimpour.legacyhub.sourcefile.application.SourceFileService;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import com.rahimpour.legacyhub.storage.application.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SourceArchiveUploadService {

    private final StorageService storageService;
    private final SourceFileService sourceFileService;
    private final FileLanguageDetector fileLanguageDetector;
    private final SourceFileIgnoreRules ignoreRules;

    public SourceArchiveUploadService(
            StorageService storageService,
            SourceFileService sourceFileService,
            FileLanguageDetector fileLanguageDetector,
            SourceFileIgnoreRules ignoreRules
    ) {
        this.storageService = storageService;
        this.sourceFileService = sourceFileService;
        this.fileLanguageDetector = fileLanguageDetector;
        this.ignoreRules = ignoreRules;
    }

    public ArchiveUploadResult uploadArchive(UUID projectId, MultipartFile archiveFile) {
        if (archiveFile == null || archiveFile.isEmpty()) {
            throw new IllegalArgumentException("Archive file must not be empty");
        }

        String originalFilename = normalizeFilename(archiveFile.getOriginalFilename());

        if (!originalFilename.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("Only ZIP archives are supported for now");
        }

        try {
            byte[] archiveBytes = archiveFile.getBytes();
            String archiveStorageKey = "projects/" + projectId + "/archives/" + originalFilename;

            storageService.store(
                    archiveStorageKey,
                    new ByteArrayInputStream(archiveBytes),
                    archiveBytes.length,
                    archiveFile.getContentType() != null ? archiveFile.getContentType() : "application/zip"
            );

            int importedFiles = 0;
            int skippedFiles = 0;

            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(archiveBytes))) {
                ZipEntry entry;

                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String entryPath = normalizeZipEntryPath(entry.getName());

                    if (entry.isDirectory() || ignoreRules.shouldSkip(entryPath)) {
                        skippedFiles++;
                        zipInputStream.closeEntry();
                        continue;
                    }
                    //TODO: this for the first ok. But later readAllBytes() schould replaced by better Streaming. bytes is not good for big Zips
                    byte[] fileBytes = zipInputStream.readAllBytes();

                    String filename = extractFilename(entryPath);
                    String extension = fileLanguageDetector.extractExtension(filename);
                    SourceFileLanguage language = fileLanguageDetector.detectByFilename(filename);
                    String contentHash = sha256(fileBytes);
                    String sourceStorageKey = "projects/" + projectId + "/source/" + entryPath;

                    storageService.store(
                            sourceStorageKey,
                            new ByteArrayInputStream(fileBytes),
                            fileBytes.length,
                            "application/octet-stream"
                    );

                    sourceFileService.createSourceFile(
                            projectId,
                            entryPath,
                            filename,
                            extension,
                            language,
                            fileBytes.length,
                            contentHash,
                            sourceStorageKey
                    );

                    importedFiles++;
                    zipInputStream.closeEntry();
                }
            }

            return new ArchiveUploadResult(projectId, archiveStorageKey, importedFiles, skippedFiles);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload source archive", e);
        }
    }

    private String normalizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Archive filename must not be empty");
        }

        return filename.replace("\\", "/");
    }

    private String normalizeZipEntryPath(String path) {
        String normalized = path.replace("\\", "/");

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (normalized.contains("..")) {
            throw new IllegalArgumentException("ZIP entry contains illegal path traversal: " + path);
        }

        return normalized;
    }

    private String extractFilename(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate SHA-256 hash", e);
        }
    }

    public record ArchiveUploadResult(
            UUID projectId,
            String archiveStorageKey,
            int importedFiles,
            int skippedFiles
    ) {
    }
}