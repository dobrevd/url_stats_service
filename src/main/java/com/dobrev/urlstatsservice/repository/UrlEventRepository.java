package com.dobrev.urlstatsservice.repository;

import com.dobrev.urlstatsservice.entity.UrlEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
public class UrlEventRepository {
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Value("${aws.urleventsddb.name}")
    private String urlEventDdbName;
    private DynamoDbAsyncTable<UrlEvent> urlEventTable;

    @PostConstruct
    private void init() {
        this.urlEventTable = dynamoDbEnhancedAsyncClient.table(
                urlEventDdbName,
                TableSchema.fromBean(UrlEvent.class)
        );
    }

    public CompletableFuture<Void> save(UrlEvent event) {
        return urlEventTable.putItem(event);
    }

    public CompletableFuture<List<UrlEvent>> findByUserIdAndEventType(String userId, String eventType) {
        List<UrlEvent> resultList = new ArrayList<>();
        CompletableFuture<List<UrlEvent>> resultFuture = new CompletableFuture<>();

        urlEventTable.index("userIdIdx")
                .query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(userId)
                                        .sortValue(eventType)
                                        .build())))
                .subscribe(collectingSubscriber(resultList, resultFuture));

        return resultFuture;
    }

    public CompletableFuture<List<UrlEvent>> findByShortUrlHashAndEventType(String hash, String eventType) {
        List<UrlEvent> resultList = new ArrayList<>();
        CompletableFuture<List<UrlEvent>> resultFuture = new CompletableFuture<>();

        urlEventTable.index("hashEventTypeIdx")
                .query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(
                                Key.builder()
                                        .partitionValue(hash)
                                        .sortValue(eventType)
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
