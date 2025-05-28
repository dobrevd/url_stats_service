package com.dobrev.urlstatsservice.controller;

import com.dobrev.urlstatsservice.UrlStatsDto;
import com.dobrev.urlstatsservice.entity.UrlEvent;
import com.dobrev.urlstatsservice.service.UrlEventsService;
import lombok.RequiredArgsConstructor;
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
public class UrlEventsController {
    private final UrlEventsService urlEventsService;

    @PostMapping
    public void save(@RequestBody UrlEvent event) {
        urlEventsService.save(event);
    }

    @GetMapping("{/id}")
    public List<UrlStatsDto> getByUserId(@PathVariable("id") String userId) {
        return urlEventsService.findByUserId(userId);
    }
}
