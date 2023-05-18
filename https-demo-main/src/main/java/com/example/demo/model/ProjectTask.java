package com.example.demo.model;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class ProjectTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String taskName;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_profile_id")
    private EngineerProfile engineerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // Constructors, getters, and setters

    public ProjectTask() {
    }

    public ProjectTask(LocalDate startDate, LocalDate endDate, String taskName, String description, EngineerProfile engineerProfile, Project project) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskName = taskName;
        this.description = description;
        this.engineerProfile = engineerProfile;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EngineerProfile getEngineerProfile() {
        return engineerProfile;
    }

    public void setEngineerProfile(EngineerProfile engineerProfile) {
        this.engineerProfile = engineerProfile;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
