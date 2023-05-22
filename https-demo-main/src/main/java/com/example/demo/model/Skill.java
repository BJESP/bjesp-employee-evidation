package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties({"engineerProfile" , "id"})
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Skill() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public EngineerProfile getEngineerProfile() {
        return engineerProfile;
    }

    public void setEngineerProfile(EngineerProfile engineerProfile) {
        this.engineerProfile = engineerProfile;
    }

    private String name;

    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_profile_id")
    private EngineerProfile engineerProfile;

    // Constructors, getters, and setters
}
