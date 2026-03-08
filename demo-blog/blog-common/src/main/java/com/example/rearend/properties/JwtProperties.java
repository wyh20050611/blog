package com.example.rearend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 34932
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端用户生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String token;
}
