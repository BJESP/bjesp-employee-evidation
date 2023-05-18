package com.example.demo.dto;

import javax.persistence.Column;
import java.time.LocalDate;

public class AddEngineerToProjectDTO {

    private Long engineerId;
    private Long projectId;
    private String taskName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public AddEngineerToProjectDTO() {
    }

    public AddEngineerToProjectDTO(Long engineerId, Long projectId, String taskName, LocalDate startDate, LocalDate endDate, String description) {
        this.engineerId = engineerId;
        this.projectId = projectId;
        this.taskName = taskName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public Long getEngineerId() {
        return engineerId;
    }

    public void setEngineerId(Long engineerId) {
        this.engineerId = engineerId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
