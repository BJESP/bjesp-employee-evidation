package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.Project;
import com.example.demo.model.ProjectManagerProfile;
import com.example.demo.model.ProjectTask;
import com.example.demo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectManagerService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private EngineerRepo engineerRepo;

    @Autowired
    private ProjectManagerRepo projectManagerRepo;

    @Autowired
    private ProjectTaskRepo projectTaskRepo;


   public List<ProjectDTO> GetAllProject(Long managerId){
        List<Project> projectList = projectRepo.findProjectsByManagersId(managerId);
        List <ProjectDTO> newList = new ArrayList<>();
        for(Project proj:projectList){

            newList.add(new ProjectDTO(proj.getName(),proj.getDuration(),proj.getDescription()));

                      }
        return newList;

    }

    public List<EngineerDTO> GetAllProjectEngineersOnProject(Long projectId){
       List<ProjectTask> projectTasks = projectTaskRepo.findAll();
       List<EngineerDTO> engineers = new ArrayList<>();
       for(ProjectTask projectTask:projectTasks){
           if(projectTask.getProject().getId()== projectId){
               engineers.add(new EngineerDTO(projectTask.getEngineerProfile().getId(), projectTask.getEngineerProfile().getFirstName(), projectTask.getEngineerProfile().getLastName(), projectTask.getTaskName(), projectTask.getDescription(), projectTask.getStartDate(),projectTask.getEndDate(),projectTask.getId()));
           }
       }

        return engineers;

    }

    public ProjectManagerProfile UpdateProjectManagerInformation(ProjectManagerUpdateDTO projectManagerUpdateDTO){
        ProjectManagerProfile projectManager = findOne(projectManagerUpdateDTO.getId());
        projectManager.setPhoneNumber(projectManagerUpdateDTO.phoneNumber);
        projectManager.setTitle(projectManagerUpdateDTO.getTitle());
        projectManager.setAddress(projectManagerUpdateDTO.getAddress());
        projectManager = projectManagerRepo.save(projectManager);
        return projectManager;

    }

    public ProjectManagerProfile findOne(Long projectManagerId){
       return projectManagerRepo.findById(projectManagerId).orElseGet(null);
    }
    public List<EngineerProfile> FindAll(){
       return engineerRepo.findAll();
    }

    public void AddEngineerToProject (AddEngineerToProjectDTO addEngineerToProjectDTO){
        EngineerProfile engineerProfile = engineerRepo.getOne(addEngineerToProjectDTO.getEngineerId());
        Project project = projectRepo.getOne(addEngineerToProjectDTO.getProjectId());
        ProjectTask projectTask = new ProjectTask(addEngineerToProjectDTO.getStartDate(),addEngineerToProjectDTO.getEndDate(), addEngineerToProjectDTO.getTaskName(), addEngineerToProjectDTO.getDescription(), engineerProfile,project);
        projectTaskRepo.save(projectTask);

    }

    public ProjectTask UpdateProjectTaskForEngineer(UpdateProjectTaskDTO updateProjectTaskDTO){
       ProjectTask  projectTask = projectTaskRepo.findById(updateProjectTaskDTO.getTaskId()).orElseGet(null);
       projectTask.setStartDate(updateProjectTaskDTO.getStartDate());
       projectTask.setEndDate(updateProjectTaskDTO.getEndDate());
       projectTask.setTaskName(updateProjectTaskDTO.getTaskName());
       projectTask.setDescription(updateProjectTaskDTO.getDescription());
       projectTaskRepo.save(projectTask);
       return projectTask;


    }
    public EngineerDTO GetEngineerTaskAndEngineer(Long taskId){
        ProjectTask  projectTask = projectTaskRepo.findById(taskId).orElseGet(null);
        EngineerDTO engineerDTO = new EngineerDTO(projectTask.getEngineerProfile().getId(), projectTask.getEngineerProfile().getFirstName(), projectTask.getEngineerProfile().getLastName(), projectTask.getTaskName(), projectTask.getDescription(), projectTask.getStartDate(),projectTask.getEndDate(),projectTask.getId());
        return engineerDTO;

    }
    public ProjectManagerProfile GetManagerById(Long managerId){
       ProjectManagerProfile manager = projectManagerRepo.findById(managerId).orElseGet(null);
       return manager;
    }

}
