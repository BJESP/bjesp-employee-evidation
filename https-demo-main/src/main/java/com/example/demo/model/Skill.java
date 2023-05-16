package com.example.demo.model;


import javax.persistence.*;

@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_profile_id")
    private EngineerProfile engineerProfile;

    // Constructors, getters, and setters
}
