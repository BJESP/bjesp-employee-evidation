package com.example.demo.dto;

import com.example.demo.model.ProjectTask;

import java.time.LocalDate;
import java.util.List;

public class EngineerDTO {
    public Long id;
    public String name;
    public String surname;
    public String taskName;
    public String description;
    public LocalDate startDate;
    public LocalDate endDate;

    public EngineerDTO( String name, String surname, String taskName, String description, LocalDate startDate, LocalDate endDate) {

        this.name = name;
        this.surname = surname;
        this.taskName = taskName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EngineerDTO(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public EngineerDTO() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


}
