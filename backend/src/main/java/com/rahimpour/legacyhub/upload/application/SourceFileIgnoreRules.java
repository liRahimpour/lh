package com.rahimpour.legacyhub.upload.application;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class SourceFileIgnoreRules {

    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            ".git",
            "bin",
            "obj",
            "target",
            "node_modules",
            ".vs",
            ".idea"
    );
    //TODO: explain why set
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            "exe",
            "dll",
            "bin",
            "obj",
            "class",
            "jar",
            "war",
            "png",
            "jpg",
            "jpeg",
            "gif",
            "webp",
            "zip",
            "rar",
            "7z",
            "tar",
            "gz"
    );

    public boolean shouldSkip(String path) {
        if (path == null || path.isBlank()) {
            return true;
        }

        String normalized = path.replace("\\", "/");

        if (normalized.endsWith("/")) {
            return true;
        }

        String[] parts = normalized.split("/");
        for (String part : parts) {
            if (IGNORED_DIRECTORIES.contains(part)) {
                return true;
            }
        }

        String extension = extractExtension(normalized);
        return IGNORED_EXTENSIONS.contains(extension);
    }

    private String extractExtension(String path) {
        int lastSlash = path.lastIndexOf('/');
        String fileName = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
