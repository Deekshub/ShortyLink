package com.deekshitha.urlshortener.util;

import java.security.SecureRandom;

public class ShortCodeGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int LENGTH = 6;

    private static final SecureRandom random =
            new SecureRandom();

    public static String generate() {

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {

            code.append(
                    CHARACTERS.charAt(
                            random.nextInt(
                                    CHARACTERS.length()
                            )
                    )
            );
        }

        return code.toString();
    }
}