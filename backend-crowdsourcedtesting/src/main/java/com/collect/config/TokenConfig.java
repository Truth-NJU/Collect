package com.collect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "token")
@Data
public class TokenConfig {

    private String secretKey = "12345678";

    private long ttl = 1800000L;

}
