package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.ProjectManagerProfile;
import com.example.demo.model.ProjectTask;
import com.example.demo.model.ValidationResult;
import com.example.demo.service.ProjectManagerService;
import com.example.demo.service.UserService;
import com.example.demo.utils.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/project-manager")
public class ProjectManagerController {

    @Autowired
    ProjectManagerService projectManagerService;
    @Autowired
    UserService userService;

    @Autowired
    UserValidation userValidation;

    @PostMapping(value="/get-projects")
    //@PreAuthorize("hasRole('PROJECT_MANAGER')")
    @PreAuthorize("hasPermission(#managerId.managerId, 'Project', 'READ')")
    public ResponseEntity<List<ProjectDTO>> GetAllProjects(@RequestBody ManagerIdDTO managerId){

           ProjectManagerProfile manager = projectManagerService.GetManagerById(managerId.getManagerId());

           List<ProjectDTO> projects = projectManagerService.GetAllProject(managerId.getManagerId());
           return new ResponseEntity<>(projects, HttpStatus.OK);

    }

    @GetMapping(value="/get-engineers/{projectId}")

    @PreAuthorize("hasPermission(#projectId, 'Engineer', 'READ')")
    public ResponseEntity<List<EngineerDTO>> GetAllEngineersFromProject(@PathVariable  Long projectId){

            List<EngineerDTO> engineers = projectManagerService.GetAllProjectEngineersOnProject(projectId);
            if (engineers == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(engineers, HttpStatus.OK);

    }
    @GetMapping(value="/get-task/{taskId}")

    @PreAuthorize("hasPermission(#taskId, 'TASK', 'READ')")
    public ResponseEntity<EngineerDTO> GetTaskAndEngineer(@PathVariable  Long taskId){

            EngineerDTO engineer = projectManagerService.GetEngineerTaskAndEngineer(taskId);
            if (engineer == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(engineer, HttpStatus.OK);

    }
    @GetMapping(value="/get-manager/{managerId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity GetManagerById(@PathVariable Long managerId){

        ProjectManagerProfile manager = projectManagerService.GetManagerById(managerId);
        return new ResponseEntity(manager,HttpStatus.OK);
    }

    @PostMapping(value="/update-project-manager")
    @PreAuthorize("hasPermission(#projectManagerUpdateDTO.id, 'Project_Manager', 'UPDATE')")
    public ResponseEntity UpdateProjectManagerInformation(@RequestBody ProjectManagerUpdateDTO projectManagerUpdateDTO){

            try {
                ValidationResult validationResult = userValidation.validEditProjectManagerDTO(projectManagerUpdateDTO);
                if (validationResult.isValid()) {
                    ProjectManagerProfile projectManager = projectManagerService.UpdateProjectManagerInformation(projectManagerUpdateDTO);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }

        }

        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value="/engineer-project")
    @PreAuthorize("hasPermission(#projectId, 'Engineer', 'READ')")
    public ResponseEntity<List<EngineerDTO>> GetAllEngineers(){

            List<EngineerProfile> engineers = projectManagerService.FindAll();
            List<EngineerDTO> engineerDTOS = new ArrayList<>();
            for (EngineerProfile engineer : engineers) {
                EngineerDTO dto = new EngineerDTO(engineer.getId(), engineer.getFirstName(), engineer.getLastName());
                engineerDTOS.add(dto);

            }
            return new ResponseEntity<>(engineerDTOS, HttpStatus.OK);


    }
    @PostMapping(value="/add-engineer")
    @PreAuthorize("hasPermission(#addEngineerToProjectDTO.projectId, 'Project_Task', 'CREATE')")
    public ResponseEntity AddEngineerToProject(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO){

            try {
                ValidationResult validationResult = userValidation.validAddEngineerToProject(addEngineerToProjectDTO);
                if (validationResult.isValid()) {
                    projectManagerService.AddEngineerToProject(addEngineerToProjectDTO);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }
            }catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

            }
    }


    @PostMapping(value="/update-project-task")
    @PreAuthorize("hasPermission(#updateProjectTaskDTO.taskId, 'Engineer_Task', 'UPDATE')")
    public ResponseEntity UpdateProjectTaskForEngineer(@RequestBody UpdateProjectTaskDTO updateProjectTaskDTO){


            try {
                ValidationResult validationResult = userValidation.validChangeEngineer(updateProjectTaskDTO);
                if (validationResult.isValid()) {
                    ProjectTask projectTask = projectManagerService.UpdateProjectTaskForEngineer(updateProjectTaskDTO);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }
            }catch(IllegalArgumentException e){
                    return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
                }

    }
    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
         return hasPermission;

    }

}
