package com.example.demo.dto;

public class ResetPasswordDTO {
    private String password;
    private String token;

    public ResetPasswordDTO() {
    }

    public ResetPasswordDTO(String password, String token) {
        this.password = password;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
