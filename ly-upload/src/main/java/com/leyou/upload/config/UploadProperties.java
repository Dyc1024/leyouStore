package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@ConfigurationProperties("ly.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;
}
