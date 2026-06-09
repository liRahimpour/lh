package com.rahimpour.legacyhub.project.infrastructure.web;

import com.rahimpour.legacyhub.project.application.ProjectService;
import com.rahimpour.legacyhub.project.domain.Project;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasAuthority('PROJECT_WRITE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request) {
        Project project = projectService.createProject(
                request.name(),
                request.description(),
                request.technologyHint()
        );

        return ProjectResponse.from(project);
    }

    //@PreAuthorize("hasAuthority('PROJECT_READ')")
    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable UUID id) {
        Project project = projectService.getProjectById(id);
        return ProjectResponse.from(project);
    }
}
