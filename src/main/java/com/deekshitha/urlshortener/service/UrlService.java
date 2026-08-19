package com.deekshitha.urlshortener.service;

import com.deekshitha.urlshortener.entity.UrlMapping;
import com.deekshitha.urlshortener.exception.ShortCodeAlreadyExistsException;
import com.deekshitha.urlshortener.repository.UrlRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.deekshitha.urlshortener.exception.ShortCodeAlreadyExistsException;
import java.util.Random;


@Service
public class UrlService {
    private static final String CHARACTERS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SHORT_CODE_LENGTH = 6;

    private final Random random = new Random();

    private final UrlRepository urlRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UrlService(
        UrlRepository urlRepository,
        BCryptPasswordEncoder passwordEncoder) {

    this.urlRepository = urlRepository;
    this.passwordEncoder = passwordEncoder;
}

    public UrlMapping saveSampleUrl() {

        UrlMapping url = new UrlMapping();

        url.setOriginalUrl("https://google.com");
        url.setShortCode("google");

        return urlRepository.save(url);
    }
    private String generateRandomCode() {

    StringBuilder code = new StringBuilder();

    for (int i = 0; i < SHORT_CODE_LENGTH; i++) {

        int randomIndex = random.nextInt(CHARACTERS.length());

        code.append(CHARACTERS.charAt(randomIndex));
    }

    return code.toString();
}
    public UrlMapping createUrl(String originalUrl,String shortCode,String password){

    if (shortCode == null || shortCode.isBlank()) {

        do {
            shortCode = generateRandomCode();
        } while (urlRepository.findByShortCode(shortCode).isPresent());

    } else {

        if (urlRepository.findByShortCode(shortCode).isPresent()) {
            throw new ShortCodeAlreadyExistsException(shortCode);
        }
    }

    UrlMapping url = new UrlMapping();

    url.setOriginalUrl(originalUrl);
    url.setShortCode(shortCode);
    if(password != null && !password.isBlank()){

    url.setPassword(
            passwordEncoder.encode(password)
    );

}

    return urlRepository.save(url);
}
public UrlMapping getByShortCode(String shortCode) {

    return urlRepository
            .findByShortCode(shortCode)
            .orElse(null);
}
public boolean verifyPassword(String shortCode, String password) {

    UrlMapping url = getByShortCode(shortCode);

    if (url == null) {
        return false;
    }

    if (url.getPassword() == null) {
        return true;
    }

    return passwordEncoder.matches(
        password,
        url.getPassword()
);
}
}