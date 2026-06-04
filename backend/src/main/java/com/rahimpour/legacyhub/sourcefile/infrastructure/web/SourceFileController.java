package com.rahimpour.legacyhub.sourcefile.infrastructure.web;

import com.rahimpour.legacyhub.sourcefile.application.SourceFileService;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/source-files")
public class SourceFileController {

    private final SourceFileService sourceFileService;

    public SourceFileController(SourceFileService sourceFileService) {
        this.sourceFileService = sourceFileService;
    }

    @GetMapping
    public List<SourceFileResponse> getSourceFilesByProjectId(@PathVariable UUID projectId) {
        return sourceFileService.getSourceFilesByProjectId(projectId)
                .stream()
                .map(SourceFileResponse::from)
                .toList();
    }

    @GetMapping("/{sourceFileId}")
    public SourceFileResponse getSourceFileById(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceFileId
    ) {
        SourceFile sourceFile = sourceFileService.getSourceFileById(projectId, sourceFileId);
        return SourceFileResponse.from(sourceFile);
    }
}