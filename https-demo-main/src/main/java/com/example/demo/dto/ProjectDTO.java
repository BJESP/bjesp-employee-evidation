package com.example.demo.dto;

public class ProjectDTO {

    private Long projectId;


    private String name;

    private int duration;

    private String description;
    private String id;




    public ProjectDTO(Long projectId,String name, int duration, String description) {

        this.name = name;
        this.duration = duration;
        this.description = description;
        this.projectId = projectId;
    }
    public ProjectDTO(String name, int duration, String description ,String projectId) {

        this.name = name;
        this.duration = duration;
        this.description = description;
        this.id = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public ProjectDTO() {
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
