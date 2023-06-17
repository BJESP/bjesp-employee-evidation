package com.example.demo.dto;

import java.util.List;

public class RolesDTO {

    List<String> roles;

    public RolesDTO(List<String> roles) {
        this.roles = roles;
    }

    public RolesDTO() {
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
