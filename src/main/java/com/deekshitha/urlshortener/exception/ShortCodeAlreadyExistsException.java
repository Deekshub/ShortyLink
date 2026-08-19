package com.deekshitha.urlshortener.exception;

public class ShortCodeAlreadyExistsException extends RuntimeException {

    public ShortCodeAlreadyExistsException(String shortCode) {
        super("Short code '" + shortCode + "' already exists.");
    }
}