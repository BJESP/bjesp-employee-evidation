package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.ProjectManagerProfile;
import com.example.demo.model.ProjectTask;
import com.example.demo.service.ProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/project-manager")
public class ProjectManagerController {

    @Autowired
    ProjectManagerService projectManagerService;

    @PostMapping(value="/get-projects")
    public ResponseEntity<List<ProjectDTO>> GetAllProjects(@RequestBody ManagerIdDTO managerId){
        try {
            List<ProjectDTO> projects = projectManagerService.GetAllProject(managerId.getManagerId());
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception ignored) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/get-engineers/{projectId}")
    public ResponseEntity<List<EngineerDTO>> GetAllEngineersFromProject(@PathVariable  Long projectId){
        List<EngineerDTO> engineers = projectManagerService.GetAllProjectEngineersOnProject(projectId);
        if (engineers == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(engineers, HttpStatus.OK);


    }
    @GetMapping(value="/get-task/{taskId}")
    public ResponseEntity<EngineerDTO> GetTaskAndEngineer(@PathVariable  Long taskId){
        EngineerDTO engineer = projectManagerService.GetEngineerTaskAndEngineer(taskId);
        if (engineer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(engineer, HttpStatus.OK);


    }
    @GetMapping(value="/get-manager/{managerId}")
    public ResponseEntity GetManagerById(@PathVariable Long managerId){
        ProjectManagerProfile manager = projectManagerService.GetManagerById(managerId);
        return new ResponseEntity(manager,HttpStatus.OK);
    }

    @PostMapping(value="/update-project-manager")
    public ResponseEntity UpdateProjectManagerInformation(@RequestBody ProjectManagerUpdateDTO projectManagerUpdateDTO){

       ProjectManagerProfile projectManager= projectManagerService.UpdateProjectManagerInformation(projectManagerUpdateDTO);
        if (projectManager == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ProjectManagerUpdateDTO(projectManager), HttpStatus.OK);


    }
    @GetMapping(value="/engineer-project")
    public ResponseEntity<List<EngineerDTO>> GetAllEngineers(){
        List<EngineerProfile> engineers = projectManagerService.FindAll();
        List<EngineerDTO> engineerDTOS = new ArrayList<>();
        for(EngineerProfile engineer: engineers){
            EngineerDTO dto = new EngineerDTO(engineer.getId(),engineer.getFirstName(),engineer.getLastName());
            engineerDTOS.add(dto);

        }
        return new ResponseEntity<>(engineerDTOS,HttpStatus.OK);
    }
    @PostMapping(value="/add-engineer")
    public ResponseEntity AddEngineerToProject(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO){
        projectManagerService.AddEngineerToProject(addEngineerToProjectDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value="/update-project-task")
    public ResponseEntity UpdateProjectTaskForEngineer(@RequestBody UpdateProjectTaskDTO updateProjectTaskDTO){

        ProjectTask projectTask = projectManagerService.UpdateProjectTaskForEngineer(updateProjectTaskDTO);
        if (projectTask == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(projectTask, HttpStatus.OK);
    }

}
