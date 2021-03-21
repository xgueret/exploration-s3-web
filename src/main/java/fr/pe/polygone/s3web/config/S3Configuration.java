package fr.pe.polygone.s3web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Configuration {
    private S3PropertiesConfiguration s3Properties;

    @Autowired
    public S3Configuration(S3PropertiesConfiguration s3Properties) {

        this.s3Properties = s3Properties;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        final AwsBasicCredentials credentials = AwsBasicCredentials.create(this.s3Properties.getUser(), this.s3Properties.getKey());
        return StaticCredentialsProvider.create(credentials);
    }

    @Bean
    public S3Client amazonS3Client(AwsCredentialsProvider awsCredentialsProvider) {

        URI endpointOverride = URI.create(this.s3Properties.getUrl());

        return S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .region(Region.EU_WEST_1)
                .endpointOverride(endpointOverride)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}
