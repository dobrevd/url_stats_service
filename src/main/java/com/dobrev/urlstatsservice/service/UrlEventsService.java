package com.dobrev.urlstatsservice.service;

import com.dobrev.urlstatsservice.entity.UrlEvent;
import com.dobrev.urlstatsservice.repository.UrlEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class UrlEventsService {
    private final UrlEventRepository urlEventRepository;

    public CompletableFuture<Void> save(UrlEvent event) {
        return urlEventRepository.save(event);
    }

    public List<UrlEvent> getByUserId(String userId) {
        try {
            return urlEventRepository.getByUserId(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch events", e);
        }
    }
}