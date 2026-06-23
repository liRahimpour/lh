package com.rahimpour.legacyhub.coderelation.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodeRelationTest {

    @Test
    void shouldCreateCodeRelation() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID sourceSymbolId = UUID.randomUUID();
        UUID targetSymbolId = UUID.randomUUID();

        // Act
        CodeRelation relation = CodeRelation.create(
                projectId,
                sourceSymbolId,
                targetSymbolId,
                CodeRelationType.HAS_METHOD
        );

        // Assert
        assertThat(relation.getId()).isNotNull();
        assertThat(relation.getProjectId()).isEqualTo(projectId);
        assertThat(relation.getSourceSymbolId()).isEqualTo(sourceSymbolId);
        assertThat(relation.getTargetSymbolId()).isEqualTo(targetSymbolId);
        assertThat(relation.getType()).isEqualTo(CodeRelationType.HAS_METHOD);
        assertThat(relation.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRejectRelationThatPointsToItself() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID sameSymbolId = UUID.randomUUID();

        // Act + Assert
        assertThatThrownBy(() ->
                CodeRelation.create(
                        projectId,
                        sameSymbolId,
                        sameSymbolId,
                        CodeRelationType.HAS_METHOD
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A code relation cannot point to itself");
    }
}