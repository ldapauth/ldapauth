package com.ldapauth.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * @description:
 * @author: orangeBabu
 * @time: 19/7/2024 PM2:51
 */

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+={}[]:;<>,.?~\\-";
    private static final String DIGITS = "0123456789";

    public static String generatePassword(int minLength, int maxLength, Integer includeLowercase,
                                           Integer includeSpecialCharacters, Integer includeDigits) {
        String characters = "";
        if(maxLength > 35) {
            maxLength = 35;
        }

        if (includeLowercase == 1) {
            characters += LOWERCASE;
        }
        if (includeSpecialCharacters == 1) {
            characters += SPECIAL_CHARACTERS;
        }
        if (includeDigits == 1) {
            characters += DIGITS;
        }

        if (includeLowercase != 1 && includeSpecialCharacters != 1 && includeDigits != 1) {
            characters += DIGITS;
        }

        SecureRandom random = new SecureRandom();
        int passwordLength = minLength + random.nextInt(maxLength - minLength + 1);

        StringBuilder password = new StringBuilder();

        while (password.length() < passwordLength) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            password.append(randomChar);
        }

        return password.toString();
    }
}
