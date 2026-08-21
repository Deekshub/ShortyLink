package com.deekshitha.urlshortener.controller;

import com.deekshitha.urlshortener.dto.CreateUrlRequest;
import com.deekshitha.urlshortener.dto.CreateUrlResponse;
import com.deekshitha.urlshortener.dto.VerifyPasswordRequest;
import com.deekshitha.urlshortener.entity.UrlMapping;
import com.deekshitha.urlshortener.service.UrlService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/create")
    public CreateUrlResponse createUrl(@RequestBody CreateUrlRequest request) {

        UrlMapping saved = urlService.createUrl(
                request.getOriginalUrl(),
                request.getShortCode(),
                request.getPassword());

        CreateUrlResponse response = new CreateUrlResponse();

        response.setId(saved.getId());
        response.setOriginalUrl(saved.getOriginalUrl());
        response.setShortCode(saved.getShortCode());
        response.setShortUrl("http://localhost:8080/" + saved.getShortCode());

        return response;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {

        UrlMapping url = urlService.getByShortCode(shortCode);

        if (url == null) {
            return ResponseEntity.notFound().build();
        }

        // If password exists, don't redirect yet
        if (url.getPassword() != null && !url.getPassword().isBlank()) {

            Map<String, Object> response = new HashMap<>();
            response.put("passwordRequired", true);
            response.put("shortCode", shortCode);

            return ResponseEntity.ok(response);
        }

        // Increment click count
        urlService.incrementClicks(url);

        // Otherwise redirect immediately
        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, url.getOriginalUrl())
                .build();
    }

    @PostMapping("/{shortCode}/verify")
    public ResponseEntity<?> verifyPassword(
            @PathVariable String shortCode,
            @RequestBody VerifyPasswordRequest request) {

        boolean valid = urlService.verifyPassword(
                shortCode,
                request.getPassword());

        if (!valid) {
            return ResponseEntity.status(401)
                    .body("Incorrect Password");
        }

        UrlMapping url = urlService.getByShortCode(shortCode);

        // Increment click count
        urlService.incrementClicks(url);

        Map<String, Object> response = new HashMap<>();
        response.put("redirectUrl", url.getOriginalUrl());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        java.util.List<UrlMapping> links = urlService.getAllLinks();

        long activeLinks = links.size();
        long totalClicks = links.stream().mapToLong(UrlMapping::getClickCount).sum();
        double averageClicks = activeLinks > 0 ? (double) totalClicks / activeLinks : 0.0;

        // Round to 1 decimal place
        averageClicks = Math.round(averageClicks * 10.0) / 10.0;

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeLinks", activeLinks);
        metrics.put("totalClicks", totalClicks);
        metrics.put("averageClicks", averageClicks);

        Map<String, Object> response = new HashMap<>();
        response.put("metrics", metrics);
        response.put("links", links);

        return ResponseEntity.ok(response);
    }
}