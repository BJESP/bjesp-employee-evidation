package com.example.demo.model;

import javax.persistence.*;

@Entity
public class CVDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] documentData;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_profile_id")
    private EngineerProfile engineerProfile;

    // Constructors, getters, and setters
}
