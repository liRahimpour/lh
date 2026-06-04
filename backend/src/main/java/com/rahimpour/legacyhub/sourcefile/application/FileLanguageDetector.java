package com.rahimpour.legacyhub.sourcefile.application;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.springframework.stereotype.Component;

import java.util.Locale;


@Component
public class FileLanguageDetector {

    public String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        String normalized = filename.replace("\\", "/");
        int lastSlash = normalized.lastIndexOf('/');
        String justName = lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;

        int dotIndex = justName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == justName.length() - 1) {
            return "";
        }

        return justName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    public SourceFileLanguage detectByFilename(String filename) {
        String extension = extractExtension(filename);

        return switch (extension) {
            case "cs" -> SourceFileLanguage.CSHARP;
            case "pas" -> SourceFileLanguage.DELPHI;
            case "dfm" -> SourceFileLanguage.DELPHI_FORM;
            case "dpr" -> SourceFileLanguage.DELPHI_PROJECT;
            case "sql" -> SourceFileLanguage.SQL;
            case "xml" -> SourceFileLanguage.XML;
            case "json" -> SourceFileLanguage.JSON;
            case "config", "properties", "yml", "yaml" -> SourceFileLanguage.CONFIG;
            default -> SourceFileLanguage.UNKNOWN;
        };
    }
}
