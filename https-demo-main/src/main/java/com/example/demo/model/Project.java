package com.example.demo.model;


import javax.persistence.*;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int duration;

    private String description;

    @ManyToMany(mappedBy = "projects")
    private List<ProjectManagerProfile> managers;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> tasks;

    public Project() {
    }
}

