package com.example.rearend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 34932
 */
@Component
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileProperties {

    private String abpath;
    private String urlpath;
    private String mdImageDir;

}
