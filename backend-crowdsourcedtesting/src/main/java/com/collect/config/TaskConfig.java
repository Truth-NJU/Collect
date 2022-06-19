package com.collect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "task")
@Data
public class TaskConfig {

    private int limit = 1000;

    private int filter = 200;

}
