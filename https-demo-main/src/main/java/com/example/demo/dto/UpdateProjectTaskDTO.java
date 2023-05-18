package com.example.demo.dto;

import com.example.demo.model.ProjectManagerProfile;
import com.example.demo.model.ProjectTask;

import javax.persistence.Column;
import java.time.LocalDate;

public class UpdateProjectTaskDTO {

    public Long taskId;


    public LocalDate startDate;


    public LocalDate endDate;

    public String taskName;

    public String description;

    public Long engineerId;
    public Long projectId;

    public UpdateProjectTaskDTO() {
    }

    public UpdateProjectTaskDTO(Long taskId, LocalDate startDate, LocalDate endDate, String taskName, String description, Long engineerId, Long projectId) {
        this.taskId = taskId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskName = taskName;
        this.description = description;
        this.engineerId = engineerId;
        this.projectId = projectId;
    }

    /*public UpdateProjectTaskDTO(ProjectTask projectTask) {
        this(projectManager.getId() ,projectManager.getAddress(),projectManager.getPhoneNumber(),projectManager.getTitle());
    }*/
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
}
