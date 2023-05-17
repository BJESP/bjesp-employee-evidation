package com.example.demo.utils;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordValidator {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    // najmanje 1 broj, najmanje 1 malo slovo, najmanje 1 veliko slovo, najmanje jedan poseban karakter(!@#&()–[{}]:;',?/*~$^+=<>), i duzine izmedju 8 i 20

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
