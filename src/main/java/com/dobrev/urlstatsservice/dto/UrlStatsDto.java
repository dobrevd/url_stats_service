package com.dobrev.urlstatsservice.dto;

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