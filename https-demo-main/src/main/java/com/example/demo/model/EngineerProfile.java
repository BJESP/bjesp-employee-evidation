package com.example.demo.model;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class EngineerProfile extends User {

    @OneToMany(mappedBy = "engineerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills;

    @OneToOne(mappedBy = "engineerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private CVDocument cvDocument;

    @OneToMany(mappedBy = "engineerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> tasks;


    // Constructors, getters, and setters
}
