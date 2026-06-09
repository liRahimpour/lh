package com.rahimpour.legacyhub.upload.infrastructure.web;

import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.infrastructure.web.SourceFileResponse;
import com.rahimpour.legacyhub.upload.application.SourceArchiveUploadService;
import com.rahimpour.legacyhub.upload.application.SourceFileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/uploads")
public class SourceFileUploadController {

    private final SourceFileUploadService sourceFileUploadService;
    private final SourceArchiveUploadService sourceArchiveUploadService;

    public SourceFileUploadController(
            SourceFileUploadService sourceFileUploadService,
            SourceArchiveUploadService sourceArchiveUploadService
    ) {
        this.sourceFileUploadService = sourceFileUploadService;
        this.sourceArchiveUploadService = sourceArchiveUploadService;
    }

    @PostMapping("/source-file")
    @ResponseStatus(HttpStatus.CREATED)
    public SourceFileResponse uploadSourceFile(
            @PathVariable UUID projectId,
            @RequestParam("file") MultipartFile file
    ) {
        SourceFile sourceFile = sourceFileUploadService.uploadSourceFile(projectId, file);
        return SourceFileResponse.from(sourceFile);
    }

    @PostMapping("/source-archive")
    @ResponseStatus(HttpStatus.CREATED)
    public SourceArchiveUploadResponse uploadSourceArchive(
            @PathVariable UUID projectId,
            @RequestParam("file") MultipartFile file
    ) {
        SourceArchiveUploadService.ArchiveUploadResult result =
                sourceArchiveUploadService.uploadArchive(projectId, file);

        return new SourceArchiveUploadResponse(
                result.projectId(),
                result.archiveStorageKey(),
                result.importedFiles(),
                result.skippedFiles()
        );
    }
}
//TODO: add documentation for this controller
/*
* Beim Upload kommt die ZIP-Datei als MultipartFile (java.util standardpacket) im Spring Controller an.
* Der SourceArchiveUploadService liest die ZIP als Byte-Array,
* speichert das OriginalZipfile über den StorageService (Storage Module) in MinIO und öffnet
* danach denselben Byte-Inhalt mit einem ZipInputStream.
* Danach wird jeder ZIP-Eintrag bzw. jeder file einzeln geprüft, ignorierte Dateien werden
* geskipt, wichtige Dateien werden gelesen, gehasht,
* sprachlich kategorisiert und separat in MinIO gespeichert.(StorageService (Storage Module))
* Für jede gespeicherte Datei wird ein SourceFile-dbRecord in
* PostgreSQL angelegt. PostgreSQL enthält also nur Metadaten und den storageKey,
* während MinIO die echten Dateiinhalte speichert.
*/