package org.app.service;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;

@Service
public class MinioFileService {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String defaultBucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(defaultBucket).build()
            );
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(defaultBucket).build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize MinIO", e);
        }
    }

    public String upload(byte[] data, String objectName) {
        try {
            // objectName = Dateiname/ID => du kannst z.B. eine UUID generieren
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(defaultBucket)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(data), data.length, -1)
                    .contentType("application/pdf")
                    .build();
            minioClient.putObject(putObjectArgs);
            return objectName; // Zurückgeben, damit wir's in DB speichern können
        } catch (Exception e) {
            throw new RuntimeException("Error uploading to MinIO", e);
        }
    }

    public byte[] download(String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(defaultBucket)
                            .object(objectName)
                            .build()
            );
            return response.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error downloading from MinIO", e);
        }
    }
}
