package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
public class GeneralValidation {
    public GeneralValidation() {
    }

    public boolean IsProperEmail(String value) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*";
        if (Pattern.compile(ePattern).matcher(value).matches())
            return true;
        return false;
    }

    public boolean IsTooShort(String value, int limit) {
        if (value.length() < limit)
            return true;
        return false;
    }

    public boolean IsTooLong(String value, int limit) {
        if (value.length() > limit)
            return true;
        return false;
    }

    public boolean HasLowercaseLetter(String value) {
        if (Pattern.compile("[a-z]+").matcher(value).find())
            return true;
        return false;
    }

    public boolean HasUppercaseLetter(String value) {
        if (Pattern.compile("[A-Z]+").matcher(value).find())
            return true;
        return false;
    }

    public boolean HasNumber(String value) {
        if (Pattern.compile("[0-9]+").matcher(value).find())
            return true;
        return false;
    }

    public boolean HasSpecialCharacter(String value) {
        if (Pattern.compile("[!@#$%^&*.,:'-/+]+").matcher(value).find())
            return true;
        return false;
    }

    public boolean HasLessOrGreaterThanCharacter(String value) {
        if (Pattern.compile("[<>]").matcher(value).find())
            return true;
        return false;
    }

    public boolean HasSpace(String value) {
        if (Pattern.compile("[ ]").matcher(value).find())
            return true;
        return false;
    }

    public boolean HasUppercaseLetterAtStartOnly(String value) {
        if (Pattern.compile("^[A-Z][a-z]+").matcher(value).matches())
            return true;
        return false;
    }
}
