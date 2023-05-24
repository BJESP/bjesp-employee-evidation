package com.example.demo.dto;

import com.example.demo.model.EngineerProfile;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

public class EngineerCVDocumentDTO {
    private String documentName;
    private MultipartFile documentData;
    private String  engineerProfileEmail;

    public EngineerCVDocumentDTO() {
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }


    public MultipartFile getDocumentData() {
        return documentData;
    }

    public void setDocumentData(MultipartFile  documentData) {
        this.documentData = documentData;
    }

    public String getEngineerProfileEmail() {
        return engineerProfileEmail;
    }

    public void setEngineerProfileEmail(String engineerProfileEmail) {
        this.engineerProfileEmail = engineerProfileEmail;
    }

}
