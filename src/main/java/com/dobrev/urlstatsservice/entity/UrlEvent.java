package com.dobrev.urlstatsservice.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@Getter
@Setter
@DynamoDbBean
public class UrlEvent {
    private String eventId;
    private String userId;
    private String eventType;
    private String shortUrlHash;
    private String originalUrl;
    private long timestamp;

    @DynamoDbPartitionKey
    public String getEventId() {
        return eventId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "userIdIdx")
    public String getUserId() {
        return userId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "hashEventTypeIdx")
    public String getShortUrlHash() {
        return shortUrlHash;
    }

    @DynamoDbSecondarySortKey(indexNames = {"userIdIdx", "hashEventTypeIdx"})
    public String getEventType() {
        return eventType;
    }
}
