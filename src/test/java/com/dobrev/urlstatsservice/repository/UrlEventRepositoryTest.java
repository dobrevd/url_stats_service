package com.dobrev.urlstatsservice.repository;

import com.dobrev.urlstatsservice.entity.UrlEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.function.Consumer;

import static com.dobrev.urlstatsservice.util.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlEventRepositoryTest {
    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Mock
    private DynamoDbAsyncTable<UrlEvent> urlEventTable;
    @Mock
    private DynamoDbAsyncIndex<UrlEvent> userIdIndex;
    private UrlEventRepository urlEventRepository;

    @BeforeEach
    void setUp() {
        urlEventRepository = new UrlEventRepository(dynamoDbEnhancedAsyncClient);
        ReflectionTestUtils.setField(urlEventRepository, "urlEventDdbTableName", "test-table");
        ReflectionTestUtils.setField(urlEventRepository, "urlEventTable", urlEventTable);
        ReflectionTestUtils.setField(urlEventRepository, "userIdIdx", "userIdIdx");
        ReflectionTestUtils.setField(urlEventRepository, "hashEventTypeIdx", "hashEventTypeIdx");
    }

    @Test
    void findByUserIdAndEventTypeShouldReturnEvents() throws Exception {
        Page<UrlEvent> mockPage = mock(Page.class);
        when(mockPage.items()).thenReturn(getUrlEvents().stream()
                .filter(e -> CREATE_EVENT_TYPE.equals(e.getEventType()))
                .toList());

        SdkPublisher<Page<UrlEvent>> sdkPublisher = SdkPublisher.adapt(Flux.just(mockPage));

        when(urlEventTable.index("userIdIdx")).thenReturn(userIdIndex);

        @SuppressWarnings("unchecked")
        Consumer<QueryEnhancedRequest.Builder> anyConsumer = (Consumer<QueryEnhancedRequest.Builder>) any(Consumer.class);

        when(userIdIndex.query(anyConsumer)).thenReturn(sdkPublisher);

        var actualResult = urlEventRepository.findByUserIdAndEventType(USER_ID, CREATE_EVENT_TYPE)
                .get();

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.size()).isEqualTo(6);
        assertThat(actualResult)
                .extracting(UrlEvent::getEventType)
                .containsOnly(CREATE_EVENT_TYPE);
    }

}
