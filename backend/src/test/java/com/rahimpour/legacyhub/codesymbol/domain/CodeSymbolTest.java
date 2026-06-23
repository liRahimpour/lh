package com.rahimpour.legacyhub.codesymbol.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodeSymbolTest {

    @Test
    void shouldCreateCodeSymbol() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();

        // Act
        CodeSymbol symbol = CodeSymbol.create(
                projectId,
                sourceFileId,
                CodeSymbolType.CLASS,
                "CustomerService",
                "com.example.customer.CustomerService",
                3,
                25
        );

        // Assert
        assertThat(symbol.getId()).isNotNull();
        assertThat(symbol.getProjectId()).isEqualTo(projectId);
        assertThat(symbol.getSourceFileId()).isEqualTo(sourceFileId);
        assertThat(symbol.getType()).isEqualTo(CodeSymbolType.CLASS);
        assertThat(symbol.getName()).isEqualTo("CustomerService");
        assertThat(symbol.getFullyQualifiedName())
                .isEqualTo("com.example.customer.CustomerService");
        assertThat(symbol.getStartLine()).isEqualTo(3);
        assertThat(symbol.getEndLine()).isEqualTo(25);
        assertThat(symbol.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldTrimSymbolName() {
        CodeSymbol symbol = CodeSymbol.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                CodeSymbolType.METHOD,
                "  saveCustomer  ",
                "com.example.CustomerService.saveCustomer",
                10,
                12
        );

        assertThat(symbol.getName()).isEqualTo("saveCustomer");
    }

    @Test
    void shouldRejectNullSymbolName() {
        assertThatThrownBy(() ->
                CodeSymbol.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CodeSymbolType.CLASS,
                        null,
                        "com.example.CustomerService",
                        1,
                        10
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Code symbol name must not be empty");
    }

    @Test
    void shouldRejectEmptySymbolName() {
        assertThatThrownBy(() ->
                CodeSymbol.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CodeSymbolType.CLASS,
                        "",
                        "com.example.CustomerService",
                        1,
                        10
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Code symbol name must not be empty");
    }

    @Test
    void shouldRejectBlankSymbolName() {
        assertThatThrownBy(() ->
                CodeSymbol.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CodeSymbolType.CLASS,
                        "   ",
                        "com.example.CustomerService",
                        1,
                        10
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Code symbol name must not be empty");
    }
}