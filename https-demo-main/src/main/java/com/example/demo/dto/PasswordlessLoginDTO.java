package com.example.demo.dto;

import java.io.Serializable;

public class PasswordlessLoginDTO implements Serializable {
    private String username;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
