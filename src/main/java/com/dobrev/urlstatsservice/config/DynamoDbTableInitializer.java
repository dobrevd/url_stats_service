package com.dobrev.urlstatsservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Component
@RequiredArgsConstructor
public class DynamoDbTableInitializer {
    private final DynamoDbClient dynamoDbClient;
    private final UrlEventsDdbProperties properties;

    @PostConstruct
    public void createTableIfNotExists() {
        String tableName = properties.getTableName();

        ListTablesResponse tables = dynamoDbClient.listTables();
        if (tables.tableNames().contains(tableName)) {
            System.out.println("Table already exists: " + tableName);
            return;
        }

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("eventId")
                        .keyType(KeyType.HASH).build())
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("eventId")
                                .attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder()
                                .attributeName("userId")
                                .attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder()
                                .attributeName("eventType")
                                .attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder()
                                .attributeName("shortUrlHash")
                                .attributeType(ScalarAttributeType.S).build()
                )
                .globalSecondaryIndexes(
                        GlobalSecondaryIndex.builder()
                                .indexName(properties.getUserIdIndex())
                                .keySchema(
                                        KeySchemaElement.builder()
                                                .attributeName("userId").keyType(KeyType.HASH).build(),
                                        KeySchemaElement.builder()
                                                .attributeName("eventType").keyType(KeyType.RANGE).build()
                                )
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder()
                                        .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                .build(),
                        GlobalSecondaryIndex.builder()
                                .indexName(properties.getHashEventTypeIdx())
                                .keySchema(
                                        KeySchemaElement.builder()
                                                .attributeName("shortUrlHash").keyType(KeyType.HASH).build(),
                                        KeySchemaElement.builder()
                                                .attributeName("eventType").keyType(KeyType.RANGE).build()
                                )
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder()
                                        .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                .build()
                )
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build();

        dynamoDbClient.createTable(request);
        System.out.println("Created table: " + tableName);
    }
}
