package com.dobrev.urlstatsservice.controller;

import com.dobrev.urlstatsservice.dto.UrlStatsDto;
import com.dobrev.urlstatsservice.entity.UrlEvent;
import com.dobrev.urlstatsservice.service.UrlEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class UrlEventsController {
    private final UrlEventsService urlEventsService;

    @PostMapping
    public void save(@RequestBody UrlEvent event) {
        urlEventsService.save(event);
        log.info("Event is saved {}", event.toString());
    }

    @GetMapping("/{id}")
    public List<UrlStatsDto> getByUserId(@PathVariable("id") String userId) {
        return urlEventsService.findByUserId(userId);
    }
}
