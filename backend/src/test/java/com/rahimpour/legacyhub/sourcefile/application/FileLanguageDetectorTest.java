package com.rahimpour.legacyhub.sourcefile.application;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.assertj.core.api.Assertions.assertThat;


class FileLanguageDetectorTest {

    @ParameterizedTest
    @CsvSource(
            value = {
                    "CustomerService.java, java",
                    "CustomerUnit.pas, pas",
                    "CustomerForm.dfm, dfm",
                    "/path/to/schema.sql, sql",
                    "C:\\\\folder\\\\file.java, java",
                    "README, ''",
                    "file., ''",
                    "'', ''"
            },
            nullValues = "null"
    )
    void shouldExtractExtension(String filename, String expectedExtension) {
        FileLanguageDetector detector = new FileLanguageDetector();

        var result = detector.extractExtension(filename);

        assertThat(result).isEqualTo(expectedExtension);
    }

    @Test
    void shouldReturnEmptyExtensionForNullFilename() {
        FileLanguageDetector detector = new FileLanguageDetector();

        var result = detector.extractExtension(null);

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "CustomerService.java, JAVA",
            "schema.sql, SQL",
            "CustomerUnit.pas, DELPHI",
            "LegacyApp.dpr, DELPHI_PROJECT",
            "CustomerForm.dfm, DELPHI_FORM",
            "image.png, UNKNOWN",
            "README, UNKNOWN"
    })
    void shouldDetectLanguageByFilename(
            String filename,
            SourceFileLanguage expectedLanguage
    ) {
        FileLanguageDetector detector = new FileLanguageDetector();

        var result = detector.detectByFilename(filename);

        assertThat(result).isEqualTo(expectedLanguage);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "image.png",
            "README",
            "archive.zip",
            "file."
    })
    void shouldReturnUnknownForUnsupportedOrInvalidFilename(String filename) {
        FileLanguageDetector detector = new FileLanguageDetector();

        var result = detector.detectByFilename(filename);

        assertThat(result).isEqualTo(SourceFileLanguage.UNKNOWN);
    }

}