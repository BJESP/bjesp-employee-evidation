package com.example.demo.dto;

import com.example.demo.model.Address;
import com.example.demo.model.ProjectManagerProfile;

import javax.persistence.Column;

public class ProjectManagerUpdateDTO {
    public Long id;

    public Address address;


    public String phoneNumber;


    public String title;

    public ProjectManagerUpdateDTO() {
    }

    public ProjectManagerUpdateDTO(Long id, Address address, String phoneNumber, String title) {
        this.id = id;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.title = title;
    }

    public ProjectManagerUpdateDTO(ProjectManagerProfile projectManager) {
        this(projectManager.getId() ,projectManager.getAddress(),projectManager.getPhoneNumber(),projectManager.getTitle());
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
