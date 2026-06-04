package com.rahimpour.legacyhub.storage.ports;

import java.io.InputStream;

public interface FileStoragePort {

    void store(String storageKey, InputStream inputStream, long size, String contentType);

    InputStream load(String storageKey);
}
