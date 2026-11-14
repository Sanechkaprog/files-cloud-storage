package com.alexkrug.files_cloud_storage.resources;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:minio.properties")
public class ResourceConfiguration {
    @Value("${minio.url}")
    private String url;

    @Value("${minio.access}")
    private String access;

    @Value("${minio.secret}")
    private String secret;

    @Bean
    public MinioClient minioClient() throws Exception{
        MinioClient minioClient = MinioClient
                .builder()
                .endpoint(url)
                .credentials(access, secret)
                .build();

        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket("cloud-storage")
                        .build()
        );

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket("cloud-storage")
                    .build());
        }

        return minioClient;
    }
}
