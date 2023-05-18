package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
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
