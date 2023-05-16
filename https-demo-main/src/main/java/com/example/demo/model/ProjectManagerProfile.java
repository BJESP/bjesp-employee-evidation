package com.example.demo.model;

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

}
