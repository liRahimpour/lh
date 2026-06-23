package com.rahimpour.legacyhub.sourcefile.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SourceFileTest {

    @Test
    void shouldCreateSourceFile() {
        // Arrange
        UUID projectId = UUID.randomUUID();

        // Act
        SourceFile sourceFile = SourceFile.create(
                projectId,
                "src/main/java/com/example/CustomerService.java",
                "CustomerService.java",
                "java",
                SourceFileLanguage.JAVA,
                1_024,
                "abc123hash",
                "projects/project-1/files/customer-service.java"
        );

        // Assert
        assertThat(sourceFile.getId()).isNotNull();
        assertThat(sourceFile.getProjectId()).isEqualTo(projectId);
        assertThat(sourceFile.getPath())
                .isEqualTo("src/main/java/com/example/CustomerService.java");
        assertThat(sourceFile.getFilename()).isEqualTo("CustomerService.java");
        assertThat(sourceFile.getExtension()).isEqualTo("java");
        assertThat(sourceFile.getLanguage()).isEqualTo(SourceFileLanguage.JAVA);
        assertThat(sourceFile.getSizeBytes()).isEqualTo(1_024);
        assertThat(sourceFile.getContentHash()).isEqualTo("abc123hash");
        assertThat(sourceFile.getStorageKey())
                .isEqualTo("projects/project-1/files/customer-service.java");
        assertThat(sourceFile.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldTrimPathAndFilename() {
        // Act
        SourceFile sourceFile = SourceFile.create(
                UUID.randomUUID(),
                "  src/CustomerService.java  ",
                "  CustomerService.java  ",
                "java",
                SourceFileLanguage.JAVA,
                100,
                null,
                null
        );

        // Assert
        assertThat(sourceFile.getPath()).isEqualTo("src/CustomerService.java");
        assertThat(sourceFile.getFilename()).isEqualTo("CustomerService.java");
    }

    @Test
    void shouldAllowZeroByteFile() {
        SourceFile sourceFile = SourceFile.create(
                UUID.randomUUID(),
                "README.md",
                "README.md",
                "md",
                SourceFileLanguage.UNKNOWN,
                0,
                null,
                null
        );

        assertThat(sourceFile.getSizeBytes()).isZero();
    }

    @Test
    void shouldRejectNullPath() {
        assertThatThrownBy(() ->
                SourceFile.create(
                        UUID.randomUUID(),
                        null,
                        "CustomerService.java",
                        "java",
                        SourceFileLanguage.JAVA,
                        100,
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("path must not be empty");
    }

    @Test
    void shouldRejectBlankPath() {
        assertThatThrownBy(() ->
                SourceFile.create(
                        UUID.randomUUID(),
                        "   ",
                        "CustomerService.java",
                        "java",
                        SourceFileLanguage.JAVA,
                        100,
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("path must not be empty");
    }

    @Test
    void shouldRejectNullFilename() {
        assertThatThrownBy(() ->
                SourceFile.create(
                        UUID.randomUUID(),
                        "src/CustomerService.java",
                        null,
                        "java",
                        SourceFileLanguage.JAVA,
                        100,
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("filename must not be empty");
    }

    @Test
    void shouldRejectBlankFilename() {
        assertThatThrownBy(() ->
                SourceFile.create(
                        UUID.randomUUID(),
                        "src/CustomerService.java",
                        "   ",
                        "java",
                        SourceFileLanguage.JAVA,
                        100,
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("filename must not be empty");
    }

    @Test
    void shouldRejectNegativeFileSize() {
        assertThatThrownBy(() ->
                SourceFile.create(
                        UUID.randomUUID(),
                        "src/CustomerService.java",
                        "CustomerService.java",
                        "java",
                        SourceFileLanguage.JAVA,
                        -1,
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sizeBytes must not be negative");
    }
}