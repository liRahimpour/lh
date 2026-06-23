package com.rahimpour.legacyhub.project.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTest {

    @Test
    void shouldCreateProjectWithDefaultStatus() {
        // Act
        Project project = Project.create(
                "Legacy CRM",
                "Analyse einer alten CRM-Anwendung",
                "Delphi, Java, SQL"
        );

        // Assert
        assertThat(project.getId()).isNotNull();
        assertThat(project.getName()).isEqualTo("Legacy CRM");
        assertThat(project.getDescription())
                .isEqualTo("Analyse einer alten CRM-Anwendung");
        assertThat(project.getTechnologyHint())
                .isEqualTo("Delphi, Java, SQL");
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.CREATED);
        assertThat(project.getCreatedAt()).isNotNull();
        assertThat(project.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldTrimProjectName() {
        // Act
        Project project = Project.create(
                "  Legacy CRM  ",
                "Beschreibung",
                "Java"
        );

        // Assert
        assertThat(project.getName()).isEqualTo("Legacy CRM");
    }

    @Test
    void shouldRejectNullProjectName() {
        assertThatThrownBy(() ->
                Project.create(null, "Beschreibung", "Java")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project name must not be empty");
    }

    @Test
    void shouldRejectEmptyProjectName() {
        assertThatThrownBy(() ->
                Project.create("", "Beschreibung", "Java")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project name must not be empty");
    }

    @Test
    void shouldRejectBlankProjectName() {
        assertThatThrownBy(() ->
                Project.create("   ", "Beschreibung", "Java")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project name must not be empty");
    }
}