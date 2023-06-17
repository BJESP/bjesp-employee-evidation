package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.EngineerProfile;
import com.example.demo.model.ProjectManagerProfile;
import com.example.demo.model.ProjectTask;
import com.example.demo.model.ValidationResult;
import com.example.demo.service.ProjectManagerService;
import com.example.demo.service.UserService;
import com.example.demo.utils.UserValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private Logger logger =  LogManager.getLogger(ProjectManagerController.class);

    @PostMapping(value="/get-projects")
    //@PreAuthorize("hasRole('PROJECT_MANAGER')")
    @PreAuthorize("hasPermission(#managerId.managerId, 'Project', 'READ')")
    public ResponseEntity GetAllProjects(@RequestBody ManagerIdDTO managerId, HttpServletRequest request){

           ProjectManagerProfile manager = projectManagerService.GetManagerById(managerId.getManagerId());
            try {
                List<ProjectDTO> projects = projectManagerService.GetAllProject(managerId.getManagerId());
                logger.info("Getting all projects for project manager");
                return new ResponseEntity<>(projects, HttpStatus.OK);
            }
            catch(NullPointerException e){
                logger.error(e.getMessage()+"during getting all projct for project anager");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

    }

    @GetMapping(value="/get-engineers/{projectId}")

    @PreAuthorize("hasPermission(#projectId, 'Engineer', 'READ')")
    public ResponseEntity<List<EngineerDTO>> GetAllEngineersFromProject(@PathVariable  Long projectId,HttpServletRequest request){

            List<EngineerDTO> engineers = projectManagerService.GetAllProjectEngineersOnProject(projectId);
            logger.info("Getting all enigneers working on project");
            if (engineers == null) {
                logger.error("List of engineers working on project is empty");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(engineers, HttpStatus.OK);

    }
    @GetMapping(value="/get-task/{taskId}")

    @PreAuthorize("hasPermission(#taskId, 'TASK', 'READ')")
    public ResponseEntity<EngineerDTO> GetTaskAndEngineer(@PathVariable  Long taskId,HttpServletRequest request){

            EngineerDTO engineer = projectManagerService.GetEngineerTaskAndEngineer(taskId);
            if (engineer == null) {
                logger.error("can't find engineer and engineer task");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            logger.info("Getting engineer task");
            return new ResponseEntity<>(engineer, HttpStatus.OK);

    }
    @GetMapping(value="/get-manager/{managerId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity GetManagerById(@PathVariable Long managerId, HttpServletRequest request){
        try {
            ProjectManagerProfile manager = projectManagerService.GetManagerById(managerId);
            logger.info("Getting project manager profle");
            return new ResponseEntity(manager, HttpStatus.OK);
        }
        catch(NullPointerException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping(value="/update-project-manager")
    @PreAuthorize("hasPermission(#projectManagerUpdateDTO.id, 'Project_Manager', 'UPDATE')")
    public ResponseEntity UpdateProjectManagerInformation(@RequestBody ProjectManagerUpdateDTO projectManagerUpdateDTO,HttpServletRequest request){

            try {

                ValidationResult validationResult = userValidation.validEditProjectManagerDTO(projectManagerUpdateDTO);
                if (validationResult.isValid()) {
                    ProjectManagerProfile projectManager = projectManagerService.UpdateProjectManagerInformation(projectManagerUpdateDTO);
                    logger.info("Successfully updated project manager information");
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else{
                    logger.error(validationResult.getErrorMessage());
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }

        }

        catch (IllegalArgumentException e) {
                logger.error(e.getMessage()+"during updating project manager information");
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
    public ResponseEntity AddEngineerToProject(@RequestBody AddEngineerToProjectDTO addEngineerToProjectDTO,HttpServletRequest request){

            try {
                ValidationResult validationResult = userValidation.validAddEngineerToProject(addEngineerToProjectDTO);
                if (validationResult.isValid()) {
                    projectManagerService.AddEngineerToProject(addEngineerToProjectDTO);
                    logger.info("Adding engineer to project");
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    logger.error(validationResult.getErrorMessage());
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }
            }catch (IllegalArgumentException e) {
                logger.error(e.getMessage()+"during adding engineer to project");
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

            }
    }


    @PostMapping(value="/update-project-task")
    @PreAuthorize("hasPermission(#updateProjectTaskDTO.taskId, 'Engineer_Task', 'UPDATE')")
    public ResponseEntity UpdateProjectTaskForEngineer(@RequestBody UpdateProjectTaskDTO updateProjectTaskDTO,HttpServletRequest request){


            try {
                ValidationResult validationResult = userValidation.validChangeEngineer(updateProjectTaskDTO);
                if (validationResult.isValid()) {
                    ProjectTask projectTask = projectManagerService.UpdateProjectTaskForEngineer(updateProjectTaskDTO);
                    logger.info("Updating project task for engineer");
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else{
                    logger.error(validationResult.getErrorMessage());
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }
            }catch(IllegalArgumentException e){
                logger.error(e.getMessage()+"during uodate project task for engineer");
                    return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
                }

    }
    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
         return hasPermission;

    }

}
