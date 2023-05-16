package com.example.demo.dto;

public class PasswordlessLoginTokenDTO {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String token;
}
