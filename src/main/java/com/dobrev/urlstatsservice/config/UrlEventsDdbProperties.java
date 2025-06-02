package com.dobrev.urlstatsservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.urleventsddb")
@Data
public class UrlEventsDdbProperties {
    private String tableName;
    private String userIdIndex;
    private String hashEventTypeIdx;
}
