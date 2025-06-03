package com.dobrev.urlstatsservice.service;

import com.dobrev.urlstatsservice.entity.UrlEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Service
@Slf4j
public class UrlEventConsumer {
    private final String urlEventsQueueUrl;
    private final ObjectMapper objectMapper;
    private final SqsAsyncClient sqsAsyncClient;
    private final ReceiveMessageRequest receiveMessageRequest;
    private final UrlEventsService urlEventsService;

    public UrlEventConsumer(@Value("${aws.sqs.queue.urlevent.url}") String urlEventsQueueUrl,
                            ObjectMapper objectMapper, SqsAsyncClient sqsAsyncClient, UrlEventsService urlEventsService) {
        this.urlEventsQueueUrl = urlEventsQueueUrl;
        this.objectMapper = objectMapper;
        this.sqsAsyncClient = sqsAsyncClient;
        this.urlEventsService = urlEventsService;
        this.receiveMessageRequest = ReceiveMessageRequest.builder()
                .maxNumberOfMessages(8)
                .queueUrl(urlEventsQueueUrl)
                .build();
    }

    @Scheduled(fixedDelay = 1000)
    public void receiveUrlEvents() {
        List<Message> messages;
        while (!(messages = sqsAsyncClient.receiveMessage(receiveMessageRequest).join().messages()).isEmpty()) {
            log.info("Reading {} messages.", messages.size());
            messages.parallelStream().forEach(message -> {
                try {
                    var urlEvent = objectMapper.readValue(message.body(), UrlEvent.class);
                    urlEventsService.save(urlEvent);
                    log.info("Saving URL event with id: {}", urlEvent.getEventId());
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse message: {}; {}", message.body(), e);
                    throw new RuntimeException(e);
                }
                sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(urlEventsQueueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build()).join();
                log.info("Deleting message with id {}.", message.receiptHandle());
            });
        }
    }
}
