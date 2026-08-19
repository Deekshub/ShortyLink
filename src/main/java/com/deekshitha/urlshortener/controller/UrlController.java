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

        Map<String, Object> response = new HashMap<>();
        response.put("redirectUrl", url.getOriginalUrl());

        return ResponseEntity.ok(response);
    }
}