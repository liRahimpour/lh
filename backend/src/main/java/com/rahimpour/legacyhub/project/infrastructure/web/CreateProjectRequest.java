package com.rahimpour.legacyhub.project.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(

        @NotBlank(message = "Project name must not be empty")
        @Size(max = 255, message = "Project name must not exceed 255 characters")
        String name,

        String description,

        @Size(max = 100, message = "Technology hint must not exceed 100 characters")
        String technologyHint
) {
}
