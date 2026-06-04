package com.rahimpour.legacyhub.storage.application;

import com.rahimpour.legacyhub.storage.ports.FileStoragePort;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class StorageService {

    private final FileStoragePort fileStoragePort;

    public StorageService(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    public void store(String storageKey, InputStream inputStream, long size, String contentType) {
        fileStoragePort.store(storageKey, inputStream, size, contentType);
    }

    public InputStream load(String storageKey) {
        return fileStoragePort.load(storageKey);
    }
}
