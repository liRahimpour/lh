package com.rahimpour.legacyhub.project.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


public class Project {

    private final UUID id;
    private final String name;
    private final String description;
    private final String technologyHint;
    private final ProjectStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Project(
            UUID id,
            String name,
            String description,
            String technologyHint,
            ProjectStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = validateName(name);
        this.description = description;
        this.technologyHint = technologyHint;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Project create(String name, String description, String technologyHint) {
        Instant now = Instant.now();

        return new Project(
                UUID.randomUUID(),
                name,
                description,
                technologyHint,
                ProjectStatus.CREATED,
                now,
                now
        );
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name must not be empty");
        }

        return name.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTechnologyHint() {
        return technologyHint;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
