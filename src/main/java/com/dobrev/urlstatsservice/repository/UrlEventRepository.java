package com.dobrev.urlstatsservice.repository;

import com.dobrev.urlstatsservice.entity.UrlEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlEventRepository {
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Value("${aws.urleventsddb.table-name}")
    private String urlEventDdbTableName;
    private DynamoDbAsyncTable<UrlEvent> urlEventTable;
    @Value("${aws.urleventsddb.userIdIndex}")
    private String userIdIdx;
    @Value("${aws.urleventsddb.hashEventTypeIdx}")
    private String hashEventTypeIdx;

    @PostConstruct
    private void init() {
        this.urlEventTable = dynamoDbEnhancedAsyncClient.table(
                urlEventDdbTableName,
                TableSchema.fromBean(UrlEvent.class)
        );
    }

    public CompletableFuture<Void> save(UrlEvent event) {
        return urlEventTable.putItem(event)
                .thenAccept(res -> log.info("Event with ID {} is saved", event.getEventId()))
                .exceptionally(ex -> {
                    log.error("Failed to save event: {}", event.getEventId(), ex);
                    return null;
                });
    }

    public CompletableFuture<List<UrlEvent>> findByUserIdAndEventType(String userId, String eventType) {
        return queryByIndex(userIdIdx, userId, eventType);
    }

    public CompletableFuture<List<UrlEvent>> findByShortUrlHashAndEventType(String hash, String eventType) {
        return queryByIndex(hashEventTypeIdx, hash, eventType);
    }

    private CompletableFuture<List<UrlEvent>> queryByIndex(
            String indexName,
            String partitionKey,
            String sortKey
    ) {
        List<UrlEvent> resultList = new ArrayList<>();
        CompletableFuture<List<UrlEvent>> resultFuture = new CompletableFuture<>();

        urlEventTable.index(indexName)
                .query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(partitionKey)
                                        .sortValue(sortKey)
                                        .build())))
                .subscribe(collectingSubscriber(resultList, resultFuture));

        return resultFuture;
    }


    private Subscriber<Page<UrlEvent>> collectingSubscriber(
            List<UrlEvent> resultList,
            CompletableFuture<List<UrlEvent>> resultFuture
    ) {
        return new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(1);
            }

            @Override
            public void onNext(Page<UrlEvent> page) {
                resultList.addAll(page.items());
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                resultFuture.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                resultFuture.complete(resultList);
            }
        };
    }
}
