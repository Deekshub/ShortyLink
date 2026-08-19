package com.deekshitha.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CreateUrlRequest {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;

    private String shortCode;

    private String password;

    private LocalDate expiryDate;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}