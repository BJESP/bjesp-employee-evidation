package com.example.demo.dto;

import java.time.LocalDate;

public class EngineerSearchDTO {
    public String name;
    public String surname;
    public String email;
    public LocalDate employedFrom;
    public LocalDate employedTo;

    public EngineerSearchDTO() {
    }

    public EngineerSearchDTO(String name, String surname, String email, LocalDate employedFrom, LocalDate employedTo) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.employedFrom = employedFrom;
        this.employedTo = employedTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getEmployedFrom() {
        return employedFrom;
    }

    public void setEmployedFrom(LocalDate employedFrom) {
        this.employedFrom = employedFrom;
    }

    public LocalDate getEmployedTo() {
        return employedTo;
    }

    public void setEmployedTo(LocalDate employedTo) {
        this.employedTo = employedTo;
    }
}
