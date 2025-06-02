package com.dobrev.urlstatsservice.service;

import com.dobrev.urlstatsservice.dto.UrlStatsDto;
import com.dobrev.urlstatsservice.entity.UrlEvent;
import com.dobrev.urlstatsservice.repository.UrlEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlEventsService {
    private static final String CREATE_EVENT = "CREATE";
    private static final String RESOLVE_EVENT = "RESOLVE";
    private final UrlEventRepository urlEventRepository;

    public void save(UrlEvent event) {
        log.info("Saving url event {}", event);
        urlEventRepository.save(event).join();
    }

    public List<UrlStatsDto> findByUserId(String userId) {
        return urlEventRepository.findByUserIdAndEventType(userId, CREATE_EVENT)
                .thenCompose(creates -> {
                    List<CompletableFuture<List<UrlEvent>>> resolveFutures = creates.stream()
                            .map(c -> urlEventRepository.findByShortUrlHashAndEventType(c.getShortUrlHash(), RESOLVE_EVENT))
                            .toList();
                    return CompletableFuture.allOf(resolveFutures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> IntStream.range(0, creates.size())
                                    .mapToObj(i -> createUrlStatsDto(
                                            creates.get(i),
                                            resolveFutures.get(i).join()))
                                    .toList());
                })
                .join();
    }

    private UrlStatsDto createUrlStatsDto(UrlEvent event, List<UrlEvent> resolves) {
        return UrlStatsDto.builder()
                .originalUrl(event.getOriginalUrl())
                .shortUrlHash(event.getShortUrlHash())
                .resolveCount(resolves.size())
                .lastResolvedAt(lastResolvedAt(resolves))
                .createdAt(event.getTimestamp())
                .build();
    }

    private long lastResolvedAt(List<UrlEvent> resolves) {
        return resolves.stream()
                .mapToLong(UrlEvent::getTimestamp)
                .max()
                .orElse(0L);
    }
}