package com.example.demo.service;

import com.example.demo.dto.AddEngineerToProjectDTO;
import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.Project;
import com.example.demo.model.ProjectTask;
import com.example.demo.model.User;
import com.example.demo.repo.ProjectRepo;
import com.example.demo.repo.ProjectTaskRepo;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepo projectRepository;
    @Autowired
    private ProjectTaskRepo projectTaskRepository;
    @Autowired
    private UserRepo userRepository;

    public List<ProjectDTO> getAll(){
        ArrayList<Project> all = (ArrayList<Project>) projectRepository.findAll();
        List<ProjectDTO> dtos = new ArrayList<>();
        for(Project p:all){
            ProjectDTO dto = new ProjectDTO(p.getName(), p.getDuration(), p.getDescription(), p.getId().toString());
            dtos.add(dto);
        }
        return dtos;
    }
    public void createProject(ProjectDTO projectDTO){
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDuration(projectDTO.getDuration());
        project.setDescription(projectDTO.getDescription());
        projectRepository.save(project);
    }
    public ArrayList<ProjectTask> getAllProjectTasksIdByProject(String projectId){
        ArrayList<ProjectTask> tasks = (ArrayList<ProjectTask>) projectTaskRepository.findAll();
        ArrayList<ProjectTask> withId = new ArrayList<>();
        for(ProjectTask task:tasks)
            if(task.getProject().getId().toString().equals(projectId))
                withId.add(task);
        return withId;
    }
    public void AddEngineerToProject (AddEngineerToProjectDTO addEngineerToProjectDTO){
        User user = userRepository.getOne(addEngineerToProjectDTO.getEngineerId());
        Project project = projectRepository.getOne(addEngineerToProjectDTO.getProjectId());
        ProjectTask projectTask = new ProjectTask();
        projectTask.setProject(project);
        projectTask.setUser(user);
        projectTask.setTaskName(addEngineerToProjectDTO.getTaskName());
        projectTask.setEndDate(addEngineerToProjectDTO.getEndDate());
        projectTask.setDescription(addEngineerToProjectDTO.getDescription());
        projectTask.setStartDate(LocalDate.now());
        projectTaskRepository.save(projectTask);

    }
}
