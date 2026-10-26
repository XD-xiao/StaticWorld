package com.example.clouddisk.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * RustFS 对象存储客户端配置。
 */
@Configuration
@RequiredArgsConstructor
public class RustFsConfig {

    private final RustFsProperties props;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .endpointOverride(URI.create(props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClientBuilder(ApacheHttpClient.builder())   // 显式使用 HttpClient 4，避免自动选到 apache5
                .forcePathStyle(true)   // RustFS/MinIO 类端点需使用路径风格访问
                .build();
    }

    /**
     * 预签名客户端（私有桶下载 / 预览），需与 S3Client 一样启用路径风格访问。
     */
    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .endpointOverride(URI.create(props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
