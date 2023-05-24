package com.example.demo.service;

import com.example.demo.dto.AddEngineerToProjectDTO;
import com.example.demo.dto.EmployeeDTO;
import com.example.demo.dto.EngineerDTO;
import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepo projectRepository;
    @Autowired
    private ProjectTaskRepo projectTaskRepository;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private EngineerRepo engineerRepo;
    @Autowired
    private ProjectManagerRepo projectManagerRepo;

    public List<ProjectDTO> getAll(){
        ArrayList<Project> all = (ArrayList<Project>) projectRepository.findAll();
        List<ProjectDTO> dtos = new ArrayList<>();
        for(Project p:all){
            ProjectDTO dto = new ProjectDTO(p.getId(), p.getName(), p.getDuration(), p.getDescription());
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
        EngineerProfile user = engineerRepo.getOne(addEngineerToProjectDTO.getEngineerId());
        Project project = projectRepository.getOne(addEngineerToProjectDTO.getProjectId());
        ProjectTask projectTask = new ProjectTask();
        projectTask.setProject(project);
        projectTask.setEngineerProfile(user);
        projectTask.setTaskName(addEngineerToProjectDTO.getTaskName());
        projectTask.setEndDate(addEngineerToProjectDTO.getEndDate());
        projectTask.setDescription(addEngineerToProjectDTO.getDescription());
        projectTask.setStartDate(LocalDate.now());
        projectTaskRepository.save(projectTask);

    }
    public ArrayList<EmployeeDTO> getAllManagersOnProject(String projectId) {
        Project p = projectRepository.getOne(Long.parseLong(projectId));
        ArrayList<EmployeeDTO> dtos = new ArrayList<>();
        List<ProjectManagerProfile> managerProfiles = p.getManagers();
        for (ProjectManagerProfile pm : managerProfiles) {
            dtos.add(new EmployeeDTO(pm.getEmail(), pm.getFirstName(), pm.getLastName(), pm.getPhoneNumber(), pm.getAddress(), pm.getTitle(), pm.getRoles().stream().iterator().next().getName()));
        }
        return dtos;
    }
    public ArrayList<EmployeeDTO> getAllManagersNotOnProject(String projectId) {
        Project p = projectRepository.getOne(Long.parseLong(projectId));
        ArrayList<EmployeeDTO> dtos = new ArrayList<>();
        List<ProjectManagerProfile> managerProfiles = p.getManagers();
        ArrayList<ProjectManagerProfile> allManagers = (ArrayList<ProjectManagerProfile>) projectManagerRepo.findAllByIsActive(true);

        Iterator<ProjectManagerProfile> iterator1 = allManagers.iterator();
        while (iterator1.hasNext()) {
            ProjectManagerProfile element1 = iterator1.next();
            Long id1 = element1.getId();
            Iterator<ProjectManagerProfile> iterator2 = managerProfiles.iterator();
            while (iterator2.hasNext()) {
                ProjectManagerProfile element2 = iterator2.next();
                if(element2.getId()!=null) {
                    Long id2 = element2.getId();
                    if (id1 == id2) {
                        iterator1.remove();
                    }
                }
            }
        }
        for (User pm : allManagers) {
            dtos.add(new EmployeeDTO(pm.getEmail(), pm.getFirstName(), pm.getLastName(), pm.getPhoneNumber(), pm.getAddress(), pm.getTitle(), pm.getRoles().stream().iterator().next().getName()));
        }
        return dtos;
    }
    public void addManagerToProject (String projectId, String email){
        ProjectManagerProfile pm = projectManagerRepo.findByEmail(email);
        Project project = projectRepository.getOne(Long.parseLong(projectId));
        List<Project> projects = pm.getProjects();
        projects.add(project);
        pm.setProjects(projects);
        projectManagerRepo.save(pm);

    }
}
