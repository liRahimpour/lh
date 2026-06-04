package com.rahimpour.legacyhub.upload.infrastructure.web;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.infrastructure.web.SourceFileResponse;
import com.rahimpour.legacyhub.upload.application.SourceFileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/uploads")
public class SourceFileUploadController {

    private final SourceFileUploadService uploadService;

    public SourceFileUploadController(SourceFileUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/source-file")
    @ResponseStatus(HttpStatus.CREATED)
    public SourceFileResponse uploadSourceFile(
            @PathVariable UUID projectId,
            @RequestParam("file") MultipartFile file
    ) {
        SourceFile sourceFile = uploadService.uploadSourceFile(projectId, file);
        return SourceFileResponse.from(sourceFile);
    }
}
