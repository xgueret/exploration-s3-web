package fr.pe.polygone.s3web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties("s3")
public class S3PropertiesConfiguration {
    private String url;
    private String namespace;
    private String bucket;
    private String user;
    private String key;
}
