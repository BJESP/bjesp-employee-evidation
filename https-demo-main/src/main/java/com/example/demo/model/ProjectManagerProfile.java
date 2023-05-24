package com.example.demo.model;

import com.example.demo.dto.RegistrationDTO;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class ProjectManagerProfile extends User {

    @ManyToMany
    @JoinTable(
            name = "PROJECT_MANAGER",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects;

    public ProjectManagerProfile() {
    }

    public ProjectManagerProfile(RegistrationDTO userRegistrationDTO) {
        super(userRegistrationDTO);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
