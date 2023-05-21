package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
public class CVDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public CVDocument() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }


    public EngineerProfile getEngineerProfile() {
        return engineerProfile;
    }

    public void setEngineerProfile(EngineerProfile engineerProfile) {
        this.engineerProfile = engineerProfile;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    private String internalName;
    private String documentName;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_profile_id")
    @JsonProperty("engineerProfileEmail")
    private EngineerProfile engineerProfile;

    // Constructors, getters, and setters
}
