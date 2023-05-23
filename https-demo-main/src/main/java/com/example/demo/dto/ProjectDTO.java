package com.example.demo.dto;

public class ProjectDTO {
    private String name;

    private int duration;

    private String description;

    private String id;

    public ProjectDTO(String name, int duration, String description) {
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    public ProjectDTO() {
    }

    public ProjectDTO(String name, int duration, String description, String id) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
