package com.rahimpour.legacyhub.project.application;

import com.rahimpour.legacyhub.project.domain.Project;
import com.rahimpour.legacyhub.project.ports.ProjectRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepositoryPort projectRepository;

    public ProjectService(ProjectRepositoryPort projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String name, String description, String technologyHint) {
        Project project = Project.create(name, description, technologyHint);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }
}
