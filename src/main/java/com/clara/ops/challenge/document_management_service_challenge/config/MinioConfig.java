package com.clara.ops.challenge.document_management_service_challenge.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

  @Value("${minio.url}")
  private String url;

  @Value("${minio.root-user}")
  private String accessKey;

  @Value("${minio.root-password}")
  private String secretKey;

  @Value("${minio.bucket}")
  private String bucketName;

  @Bean
  public MinioClient minioClient() {
    MinioClient client = MinioClient.builder()
            .endpoint(url)
            .credentials(accessKey, secretKey)
            .build();
    try {
      // Crear bucket si no existe
      boolean found = client.bucketExists(
              io.minio.BucketExistsArgs.builder().bucket(bucketName).build()
      );
      if (!found) {
        client.makeBucket(
                io.minio.MakeBucketArgs.builder().bucket(bucketName).build()
        );
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al garantizar existencia del bucket", e);
    }
    return client;
  }
}
