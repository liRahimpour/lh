package com.rahimpour.legacyhub.project.ports;

import com.rahimpour.legacyhub.project.domain.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findAll();
}
