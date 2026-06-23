package com.rahimpour.legacyhub.upload.application;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileIgnoreRulesTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            "   ",
            "target/",
            "target/app.jar",
            "target/app.jar",
            "node_modules/react/index.js",
            "src/logo.png",
            ".git/config",
            "lib/legacy.dll",
            "C:\\project\\bin\\app.exe"
    })
    void shouldSkipIgnoredFilesAndDirectories(String path) {
        SourceFileIgnoreRules rules = new SourceFileIgnoreRules();

        boolean result = rules.shouldSkip(path);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/Main.java",
            "src/main/resources/application.yml",
            "legacy/CustomerUnit.pas",
            "database/schema.sql",
            "forms/CustomerForm.dfm",
            "README.md"
    })
    void shouldNotSkipSupportedSourceFiles(String path) {
        SourceFileIgnoreRules rules = new SourceFileIgnoreRules();

        boolean result = rules.shouldSkip(path);

        assertThat(result).isFalse();
    }
}