package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EngineerSkillDTO {

    public EngineerSkillDTO(){};
    private String name;

    private int rating;

    @JsonProperty("engineerProfileEmail")
    private String engineerProfileEmail;
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

    public String getEngineerProfileEmail() {
        return engineerProfileEmail;
    }

    public void setEngineerProfileEmail(String engineerProfileEmail) {
        this.engineerProfileEmail = engineerProfileEmail;
    }


}
