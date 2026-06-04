package com.rahimpour.legacyhub.storage.infrastructure.minio;

import com.rahimpour.legacyhub.storage.ports.FileStoragePort;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class MinioFileStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioFileStorageAdapter(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
        ensureBucketExists();
    }

    @Override
    public void store(String storageKey, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(storageKey)
                            .stream(inputStream, size, -1L)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to store file in MinIO: " + storageKey, e);
        }
    }

    @Override
    public InputStream load(String storageKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(storageKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load file from MinIO: " + storageKey, e);
        }
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build()
                );
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to ensure MinIO bucket exists: " + properties.getBucket(), e);
        }
    }
}
