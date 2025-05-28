package com.dobrev.urlstatsservice;

import lombok.Builder;

@Builder
public record UrlStatsDto(
        String shortUrlHash,
        String originalUrl,
        long createdAt,
        int resolveCount,
        long lastResolvedAt
) {
}